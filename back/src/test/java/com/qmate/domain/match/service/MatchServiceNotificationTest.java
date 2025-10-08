package com.qmate.domain.match.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.qmate.common.push.PushSender;
import com.qmate.domain.event.service.EventAnniversaryService;
import com.qmate.domain.match.Match;
import com.qmate.domain.match.MatchMember;
import com.qmate.domain.match.model.request.MatchJoinRequest;
import com.qmate.domain.match.repository.MatchMemberRepository;
import com.qmate.domain.match.repository.MatchRepository;
import com.qmate.domain.notification.entity.Notification;
import com.qmate.domain.notification.repository.NotificationRepository;
import com.qmate.domain.notification.service.NotificationService;
import com.qmate.domain.pet.service.PetService;
import com.qmate.domain.questioninstance.service.RandomAdminQuestionService;
import com.qmate.domain.user.User;
import com.qmate.domain.user.UserRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.qmate.common.redis.RedisHelper;

@ExtendWith(MockitoExtension.class)
class MatchServiceNotificationTest {

  @InjectMocks
  private MatchService sut;

  @Mock private MatchRepository matchRepository;
  @Mock private MatchMemberRepository matchMemberRepository;
  @Mock private UserRepository userRepository;
  @Mock private RedisHelper redisHelper;
  @Mock private NotificationRepository notificationRepository;
  @Mock private PushSender pushSender;
  @Mock private PetService petService;
  @Mock private NotificationService notificationService;
  @Mock private EventAnniversaryService eventAnniversaryService;
  @Mock private RandomAdminQuestionService randomAdminQuestionService;
  @Test
  @DisplayName("매칭 성공 알림: 두 멤버 모두 알림 기록이 생성되고, pushEnabled=true인 멤버에게만 푸시가 발송된다")
  void joinMatch_sendsNotification_whenMatchCompleted() {
    // given: 1번 유저(푸시 ON)와 2번 유저(푸시 OFF)가 매칭되는 상황
    Long matchId = 1L;
    User user1 = User.builder().id(1L).nickname("푸시ON유저").pushEnabled(true).build();
    User user2 = User.builder().id(2L).nickname("푸시OFF유저").pushEnabled(false).build();
    Match match = Match.builder().id(matchId).build();
    match.addMember(MatchMember.create(user1, match)); // 초대자가 이미 방에 있는 상태

    var request = new MatchJoinRequest();
    request.setInviteCode("123456");

    given(userRepository.findById(2L)).willReturn(Optional.of(user2));
    given(redisHelper.getMatchIdByInviteCode("123456")).willReturn(Optional.of(matchId));
    given(matchRepository.findById(matchId)).willReturn(Optional.of(match));
    given(matchMemberRepository.findAllByMatch_Id(matchId)).willReturn(List.of(match.getMembers().get(0)));

    // when: 2번 유저가 매칭에 참여하면
    sut.joinMatch(request, 2L);

    // then:
    // 1. NotificationRepository의 save가 '총 2번' 호출되었는지 검증 (두 멤버 모두 기록은 남아야 함)
    verify(notificationRepository, times(2)).save(any(Notification.class));

    // 2. PushSender의 send가 '오직 1번만' 호출되었는지 검증 (푸시 ON인 유저에게만)
    verify(pushSender, times(1)).send(any(Notification.class));
  }
}