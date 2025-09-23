package com.qmate.domain.match.service;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.qmate.common.redis.RedisHelper;
import com.qmate.domain.match.MatchStatus;
import com.qmate.domain.match.model.request.MatchJoinRequest;
import com.qmate.domain.match.repository.MatchMemberRepository;
import com.qmate.domain.match.repository.MatchRepository;
import com.qmate.domain.user.User;
import com.qmate.domain.user.UserRepository;
import com.qmate.exception.custom.matchinstance.InviteAttemptLockedException;
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
