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
}
