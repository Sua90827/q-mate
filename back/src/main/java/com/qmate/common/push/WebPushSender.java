package com.qmate.common.push;

import com.qmate.domain.notification.entity.Notification;
import com.qmate.domain.notification.entity.PushSubscription;
import com.qmate.domain.notification.repository.PushSubscriptionRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.martijndwars.webpush.PushService;
import nl.martijndwars.webpush.Subscription;
import org.apache.http.HttpResponse;

/**
 * - Notification만 받아서:
 * 1) userId 기반 구독 전부 조회
 * 2) 각 구독에 최소 페이로드(title, notificationId) 전송
 * 3) 404/410 응답 시 해당 구독 즉시 폐기
 * - 푸시 설정 OFF 여부 체크는 호출 측에서 수행
 */
@Slf4j
@RequiredArgsConstructor
public class WebPushSender implements PushSender {

  private final PushService pushService;
  private final PushSubscriptionRepository pushSubscriptionRepository;

  @Override
  public void send(Notification notification) {
    if (notification == null) {
      return;
    }

    final Long userId = notification.getUserId();
    final Long notificationId = notification.getId();

    // 1) 구독 조회
    List<PushSubscription> subs = pushSubscriptionRepository.findAllByUserId(userId);
    if (subs.isEmpty()) {
      log.debug("[notification:{}] user:{} no subscriptions → skip", notificationId, userId);
      return;
    }

    // 2) 최소 페이로드(title, notificationId)
    final String payload = minimalPayload(notification.getPushTitle(), notificationId);

    // 3) 각 구독에 전송 (개별 실패 허용, 404/410은 구독 삭제)
    for (PushSubscription sub : subs) {
      try {
        Subscription.Keys keys = new Subscription.Keys(sub.getKeyP256dh(), sub.getKeyAuth());
        Subscription subscription = new Subscription(sub.getEndpoint(), keys);

        // 클래스명 중복 때문에 nl.martijndwars.webpush.Notification 임에 주의
        HttpResponse resp = pushService.send(new nl.martijndwars.webpush.Notification(subscription, payload));

        int status = resp.getStatusLine().getStatusCode();

        if (status == 404 || status == 410) {
          // 사용 불가 구독 폐기
          pushSubscriptionRepository.delete(sub);
          log.info("Deleted invalid subscription. user={} subId={} status={}",
              userId, sub.getId(), status);
        } else if (status < 200 || status >= 300) {
          log.warn("Non-2xx push response. user={} subId={} status={}",
              userId, sub.getId(), status);
        }
        log.info("Push sent. user={} subId={} notif={} status={}",
            userId, sub.getId(), notificationId, status);
      } catch (Exception e) {
        log.error("Push send failed. user={} subId={} notif={}",
            userId, sub.getId(), notificationId, e);
      }
    }
  }

  private String minimalPayload(String title, Long notificationId) {
    return "{\"title\":\"" + escape(title) + "\",\"notificationId\":" + notificationId + "}";
  }

  private String escape(String s) {
    if (s == null) {
      return "";
    }
    return s.replace("\\", "\\\\").replace("\"", "\\\"");
  }

}
