package com.qmate.domain.event.service;

import com.qmate.common.push.PushSender;
import com.qmate.domain.event.repository.EventQueryRepository;
import com.qmate.domain.event.repository.EventQueryRepository.DueEventRow;
import com.qmate.domain.event.repository.EventRepository;
import com.qmate.domain.match.repository.MatchMemberRepository;
import com.qmate.domain.notification.entity.Notification;
import com.qmate.domain.notification.entity.NotificationCategory;
import com.qmate.domain.notification.entity.NotificationCode;
import com.qmate.domain.notification.entity.NotificationResourceType;
import com.qmate.domain.notification.repository.NotificationRepository;
import com.qmate.domain.user.User;
import com.qmate.domain.user.UserRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Service
@RequiredArgsConstructor
public class EventAlarmService {

  private final EventRepository eventRepository;
  private final MatchMemberRepository matchMemberRepository;
  private final UserRepository userRepository;
  private final NotificationRepository notificationRepository;
  private final PushSender pushSender;

  @Transactional
  public int registerAndSendAll(LocalDate today) {
    // 1) due 이벤트 일괄 조회
    List<DueEventRow> due = eventRepository.findDueEventAlarmRows(today);
    if (due.isEmpty()) {
      return 0;
    }

    // 2) 매치별 멤버 userIds 수집 및 캐시
    Map<Long, List<Long>> matchToUserIds = new HashMap<>();
    Set<Long> allUserIds = new HashSet<>();
    for (Long matchId : due.stream().map(EventQueryRepository.DueEventRow::matchId).collect(Collectors.toSet())) {
      List<Long> userIds = matchMemberRepository.findAllUser_IdByMatch_Id(matchId);
      matchToUserIds.put(matchId, userIds);
      allUserIds.addAll(userIds);
    }
    if (allUserIds.isEmpty()) {
      return 0;
    }

    // 3) 알림 생성
    List<Notification> toSave = new ArrayList<>();
    for (DueEventRow r : due) {
      NotificationCode code = NotificationCode.valueOf(r.code());
      for (Long uid : matchToUserIds.getOrDefault(r.matchId(), List.of())) {

        Notification n = Notification.builder()
            .userId(uid)
            .matchId(r.matchId())
            .category(NotificationCategory.EVENT)
            .code(code)
            .listTitle(code.getDescription() + " " + r.title())
            .pushTitle(code.getDescription())
            .resourceType(NotificationResourceType.EVENT)
            .resourceId(r.eventId())
            .build();
        toSave.add(n);
      }
    }
    if (toSave.isEmpty()) {
      return 0;
    }

    // 4) 저장
    List<Notification> saved = notificationRepository.saveAll(toSave);

    // 5) 커밋 후 푸시 전송 (설정/푸시 가능 여부 체크는 '전송 단계'에서만)
    TransactionSynchronizationManager.registerSynchronization(
        new TransactionSynchronization() {
          @Override
          public void afterCommit() {
            Set<Long> enabledUserIds = userRepository
                .findAllById(saved.stream().map(Notification::getUserId).collect(Collectors.toSet()))
                .stream().filter(User::isPushEnabled).map(User::getId).collect(Collectors.toSet());
            if (enabledUserIds.isEmpty()) {
              return;
            }
            for (Notification n : saved) {
              if (!enabledUserIds.contains(n.getUserId())) {
                continue;
              }
              try {
                pushSender.send(n);
              } catch (Exception ignore) {
                // 전송 실패는 WebPushSender에서 로깅/정리하므로 무시
              }

            }
          }
        }
    );
    return saved.size();
  }

}
