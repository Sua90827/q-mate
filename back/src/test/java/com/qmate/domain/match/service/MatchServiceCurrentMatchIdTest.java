package com.qmate.domain.match.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.qmate.domain.event.service.EventAnniversaryService;
import com.qmate.domain.match.MatchStatus;
import com.qmate.domain.match.Match;
import com.qmate.domain.match.MatchMember;
import com.qmate.domain.match.model.request.MatchJoinRequest;
import com.qmate.domain.match.repository.MatchMemberRepository;
import com.qmate.domain.match.repository.MatchRepository;
import com.qmate.domain.notification.repository.NotificationRepository;
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
class MatchServiceCurrentMatchIdTest {

  @InjectMocks
  private MatchService sut;

  @Mock
  private MatchRepository matchRepository;
  @Mock
  private MatchMemberRepository matchMemberRepository;
  @Mock
  private UserRepository userRepository;
  @Mock
  private RedisHelper redisHelper;
  @Mock
  private PetService petService;
  @Mock
  private NotificationRepository notificationRepository;
  @Mock
  private EventAnniversaryService eventAnniversaryService;
  @Mock
  RandomAdminQuestionService randomAdminQuestionService;

  @Test
  @DisplayName("매칭 참여 성공: 두 멤버의 current_match_id가 업데이트된다")
  void joinMatch_success_updatesCurrentMatchId() {
    // given: 3번 유저(초대자)와 4번 유저(참여자)가 5번 매칭에 참여하는 상황
    Long matchId = 5L;
    Long inviterId = 3L;
    Long joinerId = 4L;

    User inviter = User.builder().id(inviterId).build();
    User joiner = User.builder().id(joinerId).build();
    Match match = Match.builder().id(matchId).build();

    MatchMember inviterMember = MatchMember.create(inviter, match);
    match.addMember(inviterMember);

    var request = new MatchJoinRequest();
    request.setInviteCode("123456");

    // Repository들의 행동을 정의(stubbing)합니다.
    given(userRepository.findById(joinerId)).willReturn(Optional.of(joiner));
    given(redisHelper.getMatchIdByInviteCode("123456")).willReturn(Optional.of(matchId));
    given(matchRepository.findById(matchId)).willReturn(Optional.of(match));

    // ▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼ 이 부분이 핵심 수정사항입니다 ▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼
    // "matchMemberRepository에게 findAllBy...를 요청하면, '초대자'가 담긴 리스트를 돌려줘" 라는 대본
    given(matchMemberRepository.findAllByMatch_Id(matchId)).willReturn(List.of(inviterMember));
    // ▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲

    // when: 4번 유저가 매칭에 참여하면
    sut.joinMatch(request, joinerId);

    // then: 두 유저의 currentMatchId가 매칭 ID로 설정되었는지 검증
    assertThat(joiner.getCurrentMatchId()).isEqualTo(matchId);
    assertThat(inviter.getCurrentMatchId()).isEqualTo(matchId);
  }

  @Test
  @DisplayName("연결 끊기 성공: 두 멤버의 current_match_id가 null로 초기화된다")
  void disconnectMatch_success_clearsCurrentMatchId() {
    // given: 5번 매칭에 3번, 4번 유저가 속해있는 상황
    Long matchId = 5L;
    Long requesterId = 3L;

    User user3 = User.builder().id(3L).build();
    User user4 = User.builder().id(4L).build();
    Match match = Match.builder().id(matchId).status(MatchStatus.ACTIVE).build();
    match.addMember(MatchMember.create(user3, match));
    match.addMember(MatchMember.create(user4, match));

    // 연결 끊기 전에는 current_match_id가 설정되어 있다고 가정
    user3.joinMatch(match);
    user4.joinMatch(match);

    given(matchRepository.findWithMembersAndUsersById(matchId)).willReturn(Optional.of(match));

    // when: 3번 유저가 연결 끊기를 요청하면
    sut.disconnectMatch(matchId, requesterId);

    // then: 두 유저의 currentMatchId가 null로 변경되었는지 검증
    assertThat(user3.getCurrentMatchId()).isNull();
    assertThat(user4.getCurrentMatchId()).isNull();
  }
}