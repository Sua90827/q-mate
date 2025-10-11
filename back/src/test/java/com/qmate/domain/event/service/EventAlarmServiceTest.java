package com.qmate.domain.event.service;

import static org.mockito.BDDMockito.*;
import static org.assertj.core.api.Assertions.*;

import com.qmate.common.push.PushSender;
import com.qmate.domain.event.entity.DueEventRow;
import com.qmate.domain.match.repository.MatchMemberRepository;
import com.qmate.domain.notification.entity.Notification;
import com.qmate.domain.notification.entity.NotificationCategory;
import com.qmate.domain.notification.entity.NotificationCode;
import com.qmate.domain.notification.entity.NotificationResourceType;
import com.qmate.domain.notification.repository.NotificationRepository;
import com.qmate.domain.user.User;
import com.qmate.domain.user.UserRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@DisplayName("EventAlarmService")
@ExtendWith(MockitoExtension.class)
class EventAlarmServiceTest {

  @Mock private MatchMemberRepository matchMemberRepository;
  @Mock private UserRepository userRepository;
  @Mock private NotificationRepository notificationRepository;
  @Mock private PushSender pushSender;
  @Mock private EventService eventService;

  @InjectMocks
  private EventAlarmService service;

  private final LocalDate today = LocalDate.of(2025, 10, 10);

  @BeforeEach
  void initSync() {
    // afterCommit 테스트용 수동 초기화
    if (!TransactionSynchronizationManager.isSynchronizationActive()) {
      TransactionSynchronizationManager.initSynchronization();
    }
  }

  @Test
  @DisplayName("due 이벤트에 대해 인박스 생성하고, pushEnabled 사용자에게만 전송")
  void register_and_send_all_basic_flow() {
    // given
    DueEventRow row1 = new DueEventRow(
        NotificationCode.EVENT_SAME_DAY.name(), 101L, 11L, "TITLE1", today);
    DueEventRow row2 = new DueEventRow(
        NotificationCode.EVENT_WEEK_BEFORE.name(), 102L, 12L, "TITLE2", today.plusDays(7));

    given(eventService.findDueEventAlarmRows(today)).willReturn(List.of(row1, row2));

    // match 11 → users: 1,2 / match 12 → users: 3
    given(matchMemberRepository.findAllUser_IdByMatch_Id(11L)).willReturn(List.of(1L, 2L));
    given(matchMemberRepository.findAllUser_IdByMatch_Id(12L)).willReturn(List.of(3L));

    // pushEnabled: user1=true, user2=false, user3=true
    User u1 = User.builder().id(1L).pushEnabled(true).build();
    User u2 = User.builder().id(2L).pushEnabled(false).build();
    User u3 = User.builder().id(3L).pushEnabled(true).build();
    given(userRepository.findAllById(Set.of(1L,2L,3L))).willReturn(List.of(u1,u2,u3));

    // 저장될 Notification 더미 id 세팅
    ArgumentCaptor<Iterable<Notification>> captor = ArgumentCaptor.forClass(Iterable.class);
    willAnswer(inv -> {
      @SuppressWarnings("unchecked")
      Iterable<Notification> it = inv.getArgument(0, Iterable.class);
      long id = 1000L;
      for (Notification n : it) n.setId(id++);
      return it;
    }).given(notificationRepository).saveAll(captor.capture());

    // when
    int created = service.registerAndSendAll(today);

    // then (DB 저장)
    assertThat(created).isEqualTo(3); // match 11: 2명, match 12: 1명 → 총 3건 생성
    Iterable<Notification> saved = captor.getValue();
    assertThat(saved).allSatisfy(n -> {
      assertThat(n.getCategory()).isEqualTo(NotificationCategory.EVENT);
      assertThat(n.getResourceType()).isEqualTo(NotificationResourceType.EVENT);
    });

    // afterCommit 강제 호출하여 전송 경로 검증
    for (TransactionSynchronization sync : TransactionSynchronizationManager.getSynchronizations()) {
      sync.afterCommit();
    }
    TransactionSynchronizationManager.clearSynchronization();

    // pushEnabled true: user1, user3 → 최소 2회 호출
    then(pushSender).should(times(2)).send(any(Notification.class));
    then(pushSender).shouldHaveNoMoreInteractions();
  }
}
