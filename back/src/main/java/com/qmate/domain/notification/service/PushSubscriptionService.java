package com.qmate.domain.notification.service;

import com.qmate.domain.notification.entity.PushSubscription;
import com.qmate.domain.notification.model.request.PushSubscriptionRegisterRequest;
import com.qmate.domain.notification.model.response.PushSubscriptionRegisterResponse;
import com.qmate.domain.notification.repository.PushSubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PushSubscriptionService {

  private final PushSubscriptionRepository pushSubscriptionRepository;

  /**
   * 구독 업서트(로그인/알림설정 ON 시 호출):
   * - endpoint 해시 기준으로 기존 레코드 조회
   * - 있으면 소유권 이전(+키 최신화), 없으면 신규 생성
   * - 저장 후 subscriptionId/createdAt/updatedAt 반환
   */
  public PushSubscriptionRegisterResponse upsert(Long userId, PushSubscriptionRegisterRequest req) {
    final String endpoint = req.getEndpoint();
    final String p256dh = req.getKeyP256dh();
    final String auth = req.getKeyAuth();

    byte[] hash = PushSubscription.sha256(endpoint);

    PushSubscription entity = pushSubscriptionRepository.findByEndpointHash(hash)
        .map(exist -> {
          exist.claimOrRefresh(userId, endpoint, p256dh, auth);
          return exist;
        })
        .orElseGet(() -> PushSubscription.builder()
            .userId(userId)
            .endpoint(endpoint)
            .endpointHash(hash)
            .keyP256dh(p256dh)
            .keyAuth(auth)
            .build()
        );

    return PushSubscriptionRegisterResponse.from(pushSubscriptionRepository.save(entity));
  }

  /**
   * 구독 해지(로그아웃/알림설정 OFF 시 호출):
   * - endpoint 해시 기준으로 기존 레코드 조회
   * - 있으면 소유권 확인 후 삭제, 없으면 무시
   */
  public void unsubscribe(Long userId, String endpoint) {
    byte[] hash = PushSubscription.sha256(endpoint);

    // 동일 endpoint가 현재 사용자 소유일 때만 삭제)
    pushSubscriptionRepository.findByEndpointHash(hash)
        .filter(sub -> userId.equals(sub.getUserId()))
        .ifPresent(pushSubscriptionRepository::delete);
  }

  /**
   * 구독 해지(subscriptionId로 직접 해지 시 호출):
   * - subscriptionId 기준으로 기존 레코드 조회
   * - 있으면 소유권 확인 후 삭제, 없으면 무시
   */
  public void unsubscribe(Long userId, Long subscriptionId) {
    pushSubscriptionRepository.findById(subscriptionId)
        .filter(sub -> userId.equals(sub.getUserId()))
        .ifPresent(pushSubscriptionRepository::delete);
  }
}
