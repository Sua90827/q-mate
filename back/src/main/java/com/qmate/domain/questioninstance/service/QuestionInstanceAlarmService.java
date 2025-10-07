package com.qmate.domain.questioninstance.service;

import com.qmate.common.push.PushSender;
import com.qmate.domain.match.MatchMember;
import com.qmate.domain.match.MatchStatus;
import com.qmate.domain.match.repository.MatchMemberRepository;
import com.qmate.domain.notification.entity.Notification;
import com.qmate.domain.notification.entity.NotificationCategory;
import com.qmate.domain.notification.entity.NotificationCode;
import com.qmate.domain.notification.entity.NotificationResourceType;
import com.qmate.domain.notification.repository.NotificationRepository;
import com.qmate.domain.questioninstance.entity.QuestionInstance;
import com.qmate.domain.questioninstance.entity.QuestionInstanceStatus;
import com.qmate.domain.questioninstance.repository.QuestionInstanceRepository;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class QuestionInstanceAlarmService {

  private final QuestionInstanceRepository qiRepository;
  private final NotificationRepository notificationRepository;
  private final PushSender pushSender;

  /**
   * 매 정각에 실행: 아직 delivered_at이 null인 PENDING QI를 발송/기록하고,
   * 매치 멤버 2명에게 알림 생성 후, 알림설정에 따라 전송한다.
   */
  @Transactional
  public void dispatchTopOfHour(ZonedDateTime nowKst) {
    int hour = nowKst.getHour();
    LocalDateTime deliveredAt = nowKst.toLocalDateTime();

    // 1) 정각 대상 QI 조회 (delivered_at IS NULL + PENDING + ACTIVE 매치 + 질문시각 일치)
    List<QuestionInstance> targets =
        qiRepository.findPendingToDeliverForHourWithMembersAndUser(hour, MatchStatus.ACTIVE, QuestionInstanceStatus.PENDING);

    // 2) delivered_at 세팅(중복 전송 방지) + 알림 생성/발송
    for (QuestionInstance qi : targets) {
      qi.setDeliveredAt(deliveredAt);
      qiRepository.save(qi); // 먼저 찍어 중복 방지

      List<MatchMember> members = qi.getMatch().getMembers();
      for (MatchMember mm : members) {
        Notification notification = Notification.builder()
            .userId(mm.getUser().getId())
            .matchId(qi.getMatch().getId())
            .category(NotificationCategory.QUESTION)
            .code(NotificationCode.QI_TODAY_READY)
            .pushTitle(NotificationCode.QI_TODAY_READY.getDescription())
            .listTitle(NotificationCode.QI_TODAY_READY.getDescription())
            .resourceType(NotificationResourceType.QUESTION_INSTANCE)
            .resourceId(qi.getId())
            .build();
        notificationRepository.save(notification);

        if (mm.getUser().isPushEnabled()) {
          pushSender.send(notification);
        }
      }
    }
  }
}
