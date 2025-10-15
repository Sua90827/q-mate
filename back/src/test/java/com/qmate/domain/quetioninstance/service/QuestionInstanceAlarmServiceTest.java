// src/test/java/com/qmate/domain/questioninstance/service/QuestionInstanceAlarmServiceTest.java
package com.qmate.domain.questioninstance.service;

import com.qmate.common.push.PushSender;
import com.qmate.domain.match.MatchMember;
import com.qmate.domain.match.repository.MatchMemberRepository;
import com.qmate.domain.notification.entity.Notification;
import com.qmate.domain.notification.repository.NotificationRepository;
import com.qmate.domain.questioninstance.entity.QuestionInstance;
import com.qmate.domain.questioninstance.entity.QuestionInstanceStatus;
import com.qmate.domain.questioninstance.repository.QuestionInstanceRepository;
import com.qmate.domain.questioninstance.repository.ReminderTargetRow;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class QuestionInstanceAlarmServiceTest {

  @InjectMocks private QuestionInstanceAlarmService service;

  @Mock private QuestionInstanceRepository qiRepository;
  @Mock private NotificationRepository notificationRepository;
  @Mock private PushSender pushSender;

  private static final ZoneId KST = ZoneId.of("Asia/Seoul");

  @Test
  @DisplayName("정각: delivered_at 미설정 PENDING QI → delivered_at 세팅 후 멤버별 알림 생성/전송")
  void dispatchTopOfHour_sendNotificationsForMembers() {
    // given
    ZonedDateTime now = ZonedDateTime.of(2025, 1, 10, 12, 0, 0, 0, KST);

    // QI + Match + Members + Users 를 전부 모킹 (fetch join으로 한 번에 로딩됐다고 가정)
    QuestionInstance qi = mock(QuestionInstance.class, RETURNS_DEEP_STUBS);

    var user1 = mock(com.qmate.domain.user.User.class);
    given(user1.getId()).willReturn(101L);
    given(user1.isPushEnabled()).willReturn(true);

    var user2 = mock(com.qmate.domain.user.User.class);
    given(user2.getId()).willReturn(102L);
    given(user2.isPushEnabled()).willReturn(false);

    MatchMember mm1 = mock(MatchMember.class, RETURNS_DEEP_STUBS);
    given(mm1.getUser()).willReturn(user1);

    MatchMember mm2 = mock(MatchMember.class, RETURNS_DEEP_STUBS);
    given(mm2.getUser()).willReturn(user2);

    given(qi.getId()).willReturn(999L);
    given(qi.getStatus()).willReturn(QuestionInstanceStatus.PENDING);
    given(qi.getMatch().getId()).willReturn(1L);
    given(qi.getMatch().getMembers()).willReturn(List.of(mm1, mm2));

    given(qiRepository.findPendingToDeliverForHourWithMembersAndUser(
        anyInt(), any(), any())
    ).willReturn(List.of(qi));

    // when
    service.dispatchTopOfHour(now);

    // then
    // delivered_at 기록
    verify(qi).setDeliveredAt(now.toLocalDateTime());
    verify(qiRepository).save(qi);

    // 알림 2건 생성
    ArgumentCaptor<Notification> notiCaptor = ArgumentCaptor.forClass(Notification.class);
    verify(notificationRepository, times(2)).save(notiCaptor.capture());
    assertThat(notiCaptor.getAllValues()).hasSize(2);

    // pushEnabled=true 인 user1 에 대해서만 실제 전송
    verify(pushSender, times(1)).send(any(Notification.class));
  }

  @Test
  @DisplayName("리마인더: 6시간 전 같은 시(0~50) delivered & 미답변 대상 → 알림 생성, pushEnabled만 푸시")
  void remindAfterSixHours_createNotiAndSendPushConditionally() {
    // given
    ZonedDateTime now = ZonedDateTime.of(2025, 1, 10, 17, 1, 0, 0, KST);
    var expectedStart = now.minusHours(6).withMinute(0).withSecond(0).withNano(0).toLocalDateTime(); // 11:00
    var expectedEnd = expectedStart.plusMinutes(50); // 11:50

    // 리마인더 대상 프젝션 스텁
    ReminderTargetRow rowPushOn = stubRow(1001L, 11L, 201L, true);
    ReminderTargetRow rowPushOff = stubRow(1002L, 11L, 202L, false);

    given(qiRepository.findReminderTargetsBetween(expectedStart, expectedEnd))
        .willReturn(List.of(rowPushOn, rowPushOff));

    // 중복 알림 없음
    given(notificationRepository.existsByUserIdAndCodeAndResourceTypeAndResourceId(
        anyLong(), any(), any(), anyLong())).willReturn(false);

    // when
    service.remindAfterSixHours(now);

    // then
    // 알림은 2건 모두 생성
    ArgumentCaptor<Notification> notiCaptor = ArgumentCaptor.forClass(Notification.class);
    verify(notificationRepository, times(2)).save(notiCaptor.capture());
    assertThat(notiCaptor.getAllValues())
        .extracting(Notification::getUserId)
        .containsExactlyInAnyOrder(201L, 202L);

    // pushEnabled=true 인 대상에게만 푸시 1회
    verify(pushSender, times(1)).send(any(Notification.class));

    // 시간창 파라미터 정확히 전달되었는지도 검증
    verify(qiRepository).findReminderTargetsBetween(expectedStart, expectedEnd);
  }

  @Test
  @DisplayName("리마인더: 동일 알림 존재 시 생성·전송 스킵")
  void remindAfterSixHours_skipIfAlreadyNotified() {
    // given
    ZonedDateTime now = ZonedDateTime.of(2025, 1, 10, 17, 5, 0, 0, KST);
    LocalDateTime start = now.minusHours(6).withMinute(0).withSecond(0).withNano(0).toLocalDateTime();
    LocalDateTime end = start.plusMinutes(50);

    ReminderTargetRow row = stubRow(1001L, 11L, 201L, true);
    given(qiRepository.findReminderTargetsBetween(start, end))
        .willReturn(List.of(row));

    // 이미 같은 알림 있음
    given(notificationRepository.existsByUserIdAndCodeAndResourceTypeAndResourceId(
        eq(201L), any(), any(), eq(1001L))).willReturn(true);

    // when
    service.remindAfterSixHours(now);

    // then
    verify(notificationRepository, never()).save(any());
    verify(pushSender, never()).send(any());
  }

  @Test
  @DisplayName("리마인더: 푸시 전송 실패해도 예외 전파 없이 진행(알림 레코드는 유지)")
  void remindAfterSixHours_pushFailureIsCaught() {
    // given
    ZonedDateTime now = ZonedDateTime.of(2025, 1, 10, 17, 10, 0, 0, KST);
    LocalDateTime start = now.minusHours(6).withMinute(0).withSecond(0).withNano(0).toLocalDateTime();
    LocalDateTime end = start.plusMinutes(50);

    ReminderTargetRow row = stubRow(1001L, 11L, 201L, true);
    given(qiRepository.findReminderTargetsBetween(start, end))
        .willReturn(List.of(row));

    given(notificationRepository.existsByUserIdAndCodeAndResourceTypeAndResourceId(anyLong(), any(), any(), anyLong()))
        .willReturn(false);

    // pushSender가 예외 던짐 → 테스트 대상 코드가 try-catch로 흡수해야 함
    willThrow(new RuntimeException("push failed")).given(pushSender).send(any(Notification.class));

    // when & then (예외 안터져야 함)
    service.remindAfterSixHours(now);

    // 알림은 저장되었고
    verify(notificationRepository, times(1)).save(any(Notification.class));
    // 푸시는 시도되었으나 실패 → 예외 전파 없음
    verify(pushSender, times(1)).send(any(Notification.class));
  }

  private static ReminderTargetRow stubRow(Long qiId, Long matchId, Long userId, boolean pushEnabled) {
    return new ReminderTargetRow() {
      @Override public Long getQiId() { return qiId; }
      @Override public Long getMatchId() { return matchId; }
      @Override public Long getUserId() { return userId; }
      @Override public Boolean getPushEnabled() { return pushEnabled; }
    };
  }
}
