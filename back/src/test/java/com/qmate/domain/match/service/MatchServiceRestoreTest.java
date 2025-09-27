package com.qmate.domain.match.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import com.qmate.domain.match.MatchStatus;
import com.qmate.domain.match.Match;
import com.qmate.domain.match.MatchMember;
import com.qmate.domain.match.repository.MatchRepository;
import com.qmate.domain.user.User;
import com.qmate.exception.custom.matchinstance.MatchForbiddenException;
import com.qmate.exception.custom.matchinstance.MatchNotFoundException;
import com.qmate.exception.custom.matchinstance.MatchRecoveryExpiredException;
import com.qmate.exception.custom.matchinstance.MatchStateConflictException;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MatchServiceRestoreTest {

  @InjectMocks
  private MatchService sut;

  @Mock
  private MatchRepository matchRepository;

  private User user1, user2;
  private Match detachedMatch;

  @BeforeEach
  void setUp() {
    // 모든 테스트에서 공통으로 사용할 가짜 데이터 준비
    user1 = User.builder().id(1L).build();
    user2 = User.builder().id(2L).build();

    // 연결이 끊긴 지 '1주일' 된 매칭을 기본 상태로 설정
    detachedMatch = Match.builder()
        .id(100L)
        .status(MatchStatus.DETACHED_PENDING_DELETE)
        .detachedAt(LocalDateTime.now().minusWeeks(1)) // 1주일 전
        .build();
    detachedMatch.addMember(MatchMember.create(user1, detachedMatch));
    detachedMatch.addMember(MatchMember.create(user2, detachedMatch));
  }

  @Test
  @DisplayName("연결 복구 성공: 상태가 ACTIVE로 변경되고 detachedAt은 null이 된다")
  void restoreMatch_success() {
    // given: 1번 유저가 자신의 DETACHED 상태인 매칭에 대해 복구를 요청
    Long matchId = 100L;
    Long requesterId = 1L;
    given(matchRepository.findWithMembersAndUsersById(matchId)).willReturn(Optional.of(detachedMatch));

    // when: 서비스 메서드를 실행
    sut.restoreMatch(matchId, requesterId);

    // then: Match 엔티티의 상태가 올바르게 변경되었는지 검증
    assertThat(detachedMatch.getStatus()).isEqualTo(MatchStatus.ACTIVE);
    assertThat(detachedMatch.getDetachedAt()).isNull();
  }

  @Test
  @DisplayName("연결 복구 실패: 존재하지 않는 매칭(404)")
  void restoreMatch_fail_matchNotFound() {
    // given: 존재하지 않는 매칭 ID로 요청
    Long nonExistentMatchId = 404L;
    given(matchRepository.findWithMembersAndUsersById(nonExistentMatchId)).willReturn(Optional.empty());

    // expect: MatchNotFoundException 예외가 발생해야 함
    assertThatThrownBy(() -> sut.restoreMatch(nonExistentMatchId, 1L))
        .isInstanceOf(MatchNotFoundException.class);
  }

  @Test
  @DisplayName("연결 복구 실패: 멤버가 아닌 사용자의 접근(403)")
  void restoreMatch_fail_forbidden() {
    // given: 100번 매칭 정보를 상관없는 99번 유저가 복구하려고 시도
    Long matchId = 100L;
    Long outsiderId = 99L;
    given(matchRepository.findWithMembersAndUsersById(matchId)).willReturn(Optional.of(detachedMatch));

    // expect: MatchForbiddenException 예외가 발생해야 함
    assertThatThrownBy(() -> sut.restoreMatch(matchId, outsiderId))
        .isInstanceOf(MatchForbiddenException.class);
  }

  @Test
  @DisplayName("연결 복구 실패: 복구할 수 없는 상태의 매칭(409)")
  void restoreMatch_fail_stateConflict() {
    // given: 매칭의 상태가 ACTIVE인 상황
    Long matchId = 100L;
    Long requesterId = 1L;
    detachedMatch.setStatus(MatchStatus.ACTIVE); // 상태를 미리 변경
    given(matchRepository.findWithMembersAndUsersById(matchId)).willReturn(Optional.of(detachedMatch));

    // expect: MatchStateConflictException 예외가 발생해야 함
    assertThatThrownBy(() -> sut.restoreMatch(matchId, requesterId))
        .isInstanceOf(MatchStateConflictException.class);
  }

  @Test
  @DisplayName("연결 복구 실패: 복구 가능 기간(2주) 만료(409)")
  void restoreMatch_fail_recoveryExpired() {
    // given: 연결이 끊긴 지 '3주'가 지난, 이 테스트만을 위한 특별한 Match 객체를 만듭니다.
    Long matchId = 100L;
    Long requesterId = 1L;
    User user1 = User.builder().id(requesterId).build();

    // setDetachedAt 대신, builder를 사용해 detachedAt 값을 처음부터 설정합니다.
    Match expiredMatch = Match.builder()
        .id(matchId)
        .status(MatchStatus.DETACHED_PENDING_DELETE)
        .detachedAt(LocalDateTime.now().minusWeeks(3)) // ◀◀◀ 3주 전으로 시간 설정
        .build();
    expiredMatch.addMember(MatchMember.create(user1, expiredMatch));

    // Repository가 이 테스트용 객체를 반환하도록 설정합니다.
    given(matchRepository.findWithMembersAndUsersById(matchId)).willReturn(Optional.of(expiredMatch));
    // ▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲

    // expect: MatchRecoveryExpiredException 예외가 발생해야 함
    assertThatThrownBy(() -> sut.restoreMatch(matchId, requesterId))
        .isInstanceOf(MatchRecoveryExpiredException.class);
  }
}