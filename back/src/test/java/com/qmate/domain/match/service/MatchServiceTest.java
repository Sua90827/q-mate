package com.qmate.domain.match.service;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.qmate.common.redis.RedisHelper;
import com.qmate.domain.match.Match;
import com.qmate.domain.match.MatchMember;
import com.qmate.domain.match.MatchStatus;
import com.qmate.domain.match.RelationType;
import com.qmate.domain.match.model.request.MatchJoinRequest;
import com.qmate.domain.match.model.response.MatchInfoResponse;
import com.qmate.domain.match.repository.MatchMemberRepository;
import com.qmate.domain.match.repository.MatchRepository;
import com.qmate.domain.user.User;
import com.qmate.domain.user.UserRepository;
import com.qmate.exception.custom.matchinstance.InviteAttemptLockedException;
import com.qmate.exception.custom.matchinstance.MatchForbiddenException;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MatchServiceJoinTest {

  @InjectMocks
  private MatchService sut; // sut: System Under Test (테스트 대상 시스템)

  @Mock
  private MatchRepository matchRepository;
  @Mock
  private MatchMemberRepository matchMemberRepository;
  @Mock
  private UserRepository userRepository;
  @Mock
  private RedisHelper redisHelper;


  @Test
  @DisplayName("매칭 정보 조회 성공")
  void getMatchInfo_success(){
    // given: 1번 매칭에 1번, 2번 유저가 속해있고, 1번 유저가 조회를 요청한 상황
    Long matchId = 1L;
    Long requesterId = 1L; // 요청자 ID

    User user1 = User.builder().id(1L).nickname("유저1").build();
    User user2 = User.builder().id(2L).nickname("유저2").build();
    Match match = Match.builder().id(matchId).relationType(RelationType.FRIEND).build();

    // Match 엔티티가 멤버 목록을 가지도록 설정
    match.addMember(MatchMember.create(user1, match));
    match.addMember(MatchMember.create(user2, match));

    // matchRepository.findById(1L)이 호출되면, 위에서 만든 가짜 match 객체를 반환하도록 설정
    given(matchRepository.findById(matchId)).willReturn(Optional.of(match));

    // when: 서비스 메서드를 실행하면
    MatchInfoResponse response = sut.getMatchInfo(matchId, requesterId);

    // then: 반환된 DTO의 내용이 정확한지 검증
    assertThat(response.getMatchId()).isEqualTo(matchId);
    assertThat(response.getRelationType()).isEqualTo(RelationType.FRIEND);
    assertThat(response.getUsers()).hasSize(2);
    assertThat(response.getUsers().get(0).getNickname()).isEqualTo("유저1");
  }

  @Test
  @DisplayName("매칭 정보 조회 실패: 멤버가 아닌 사용자의 접근")
  void getMatchInfo_fail_forbidden() {
    // given: 1번 매칭에 1번, 2번 유저가 속해있는데, 상관없는 99번 유저가 조회를 요청한 상황
    Long matchId = 1L;
    Long outsiderId = 99L; // 외부인 ID

    User user1 = User.builder().id(1L).build();
    User user2 = User.builder().id(2L).build();
    Match match = Match.builder().id(matchId).build();
    match.addMember(MatchMember.create(user1, match));
    match.addMember(MatchMember.create(user2, match));

    given(matchRepository.findById(matchId)).willReturn(Optional.of(match));

    // expect: getMatchInfo를 호출하면 MatchForbiddenException 예외가 발생해야 함
    assertThatThrownBy(() -> sut.getMatchInfo(matchId, outsiderId))
        .isInstanceOf(MatchForbiddenException.class);
  }

  @Test
  @DisplayName("매칭 참여 실패: 초대 코드 5회 시도 시 계정이 잠긴다")
  void joinMatch_fail_locksAfter5Attempts() {
    // given: 4번 유저가 잘못된 코드를 입력하는 상황
    Long joinerId = 4L;
    var request = new MatchJoinRequest();
    request.setInviteCode("000000"); // 잘못된 코드
    var joiner = User.builder().id(joinerId).build();

    given(redisHelper.isLocked(joinerId)).willReturn(false); // 잠겨있지 않음
    given(userRepository.findById(joinerId)).willReturn(Optional.of(joiner));
    given(matchMemberRepository.findByUser_IdAndMatch_Status(joinerId, MatchStatus.ACTIVE))
        .willReturn(Optional.empty()); // 다른 매칭에 참여 중이지 않음
    given(redisHelper.getMatchIdByInviteCode("000000")).willReturn(Optional.empty()); // 코드 조회 실패

    // 중요: 시도 횟수 증가 시, '5'를 반환하도록 설정
    given(redisHelper.incrementAttemptCount(joinerId)).willReturn(5L);

    // expect: InviteAttemptLockedException 예외가 발생해야 함
    assertThatThrownBy(() -> sut.joinMatch(request, joinerId))
        .isInstanceOf(InviteAttemptLockedException.class);

    // then: lockUser 메서드가 1번 호출되었는지 검증
    verify(redisHelper).lockUser(joinerId);
  }

  @Test
  @DisplayName("매칭 참여 실패: 이미 잠긴 계정으로 참여 시도")
  void joinMatch_fail_alreadyLocked() {
    // given: 3번 유저가 이미 잠겨있는 상황
    Long joinerId = 3L;
    var request = new MatchJoinRequest();
    request.setInviteCode("123456");

    // 중요: 잠겨있다고 설정
    given(redisHelper.isLocked(joinerId)).willReturn(true);

    // expect: InviteAttemptLockedException 예외가 발생해야 함
    assertThatThrownBy(() -> sut.joinMatch(request, joinerId))
        .isInstanceOf(InviteAttemptLockedException.class);

    // then: 다른 어떤 메서드도 호출되지 않았는지 검증
    verify(userRepository, never()).findById(any());
    verify(redisHelper, never()).incrementAttemptCount(any());
  }

}
