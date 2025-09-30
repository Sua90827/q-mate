package com.qmate.domain.match.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import com.qmate.domain.match.Match;
import com.qmate.domain.match.MatchMember;
import com.qmate.domain.match.MatchStatus;
import com.qmate.domain.match.repository.MatchRepository;
import com.qmate.domain.user.User;
import com.qmate.exception.custom.matchinstance.MatchForbiddenException;
import com.qmate.exception.custom.matchinstance.MatchNotFoundException;
import com.qmate.exception.custom.matchinstance.MatchStateConflictException;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class MatchServiceDisconnectTest {

  @InjectMocks
  private MatchService sut;

  @Mock
  private MatchRepository matchRepository;

  private User user1, user2;
  private Match activeMatch;

  @BeforeEach
  void setup(){
    // 모든 테스트에서 공통으로 사용할 가짜 데이터 준비
    user1 = User.builder().id(1L).build();
    user2 = User.builder().id(2L).build();
    activeMatch = Match.builder().id(100L).status(MatchStatus.ACTIVE).build();
    activeMatch.addMember(MatchMember.create(user1, activeMatch));
    activeMatch.addMember(MatchMember.create(user2, activeMatch));

  }
  @Test
  @DisplayName("연결 끊기 성공: 상태가 DETACHED_PENDING_DELETE로 변경된다")
  void disconnectMatch_success() {
    // given: 1번 유저가 자신의 ACTIVE 상태인 매칭에 대해 연결 끊기를 요청
    Long matchId = 100L;
    Long requesterId = 1L;
    given(matchRepository.findWithMembersAndUsersById(matchId)).willReturn(Optional.of(activeMatch));

    // when: 서비스 메서드를 실행
    sut.disconnectMatch(matchId, requesterId);

    // then: Match 엔티티의 상태가 올바르게 변경되었는지 검증
    assertThat(activeMatch.getStatus()).isEqualTo(MatchStatus.DETACHED_PENDING_DELETE);
    assertThat(activeMatch.getDetachedAt()).isNotNull();
  }

  @Test
  @DisplayName("연결 끊기 실패: 존재하지 않는 매칭(404)")
  void disconnectMatch_fail_matchNotFound() {
    // given: 존재하지 않는 매칭 ID로 요청
    Long nonExistentMatchId = 404L;
    given(matchRepository.findWithMembersAndUsersById(nonExistentMatchId)).willReturn(Optional.empty());

    // expect: MatchNotFoundException 예외가 발생해야 함
    assertThatThrownBy(() -> sut.disconnectMatch(nonExistentMatchId, 1L))
        .isInstanceOf(MatchNotFoundException.class);
  }

  @Test
  @DisplayName("연결 끊기 실패: 멤버가 아닌 사용자의 접근(403)")
  void disconnectMatch_fail_forbidden() {
    // given: 100번 매칭 정보를 상관없는 99번 유저가 끊으려고 시도
    Long matchId = 100L;
    Long outsiderId = 99L;
    given(matchRepository.findWithMembersAndUsersById(matchId)).willReturn(Optional.of(activeMatch));

    // expect: MatchForbiddenException 예외가 발생해야 함
    assertThatThrownBy(() -> sut.disconnectMatch(matchId, outsiderId))
        .isInstanceOf(MatchForbiddenException.class);
  }
  @Test
  @DisplayName("연결 끊기 실패: 이미 끊어진 매칭을 또 끊으려는 시도(409)")
  void disconnectMatch_fail_stateConflict() {
    // given: 매칭의 상태가 ACTIVE가 아닌 DETACHED_PENDING_DELETE인 상황
    Long matchId = 100L;
    Long requesterId = 1L;
    activeMatch.setStatus(MatchStatus.DETACHED_PENDING_DELETE); // 상태를 미리 변경
    given(matchRepository.findWithMembersAndUsersById(matchId)).willReturn(Optional.of(activeMatch));

    // expect: MatchStateConflictException 예외가 발생해야 함
    assertThatThrownBy(() -> sut.disconnectMatch(matchId, requesterId))
        .isInstanceOf(MatchStateConflictException.class);
  }

}
