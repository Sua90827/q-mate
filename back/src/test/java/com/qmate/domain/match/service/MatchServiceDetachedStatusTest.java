package com.qmate.domain.match.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.qmate.domain.match.MatchStatus;
import com.qmate.domain.match.model.response.DetachedMatchStatusResponse;
import com.qmate.domain.match.Match;
import com.qmate.domain.match.MatchMember;
import com.qmate.domain.match.repository.MatchMemberRepository;
import com.qmate.domain.user.User;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MatchServiceDetachedStatusTest {

  @InjectMocks
  private MatchService sut;

  @Mock
  private MatchMemberRepository matchMemberRepository;

  @Test
  @DisplayName("복구 가능 매칭 조회: 복구할 매칭이 있는 경우")
  void getDetachedMatchStatus_whenMatchExists() {
    // given: 1번 유저에게 복구 가능한 100번 매칭이 있는 상황
    Long userId = 1L;
    Long matchId = 100L;
    User user = User.builder().id(userId).build();
    Match match = Match.builder().id(matchId).build();
    MatchMember detachedMember = MatchMember.create(user, match);

    given(matchMemberRepository.findDetachedMatchForUser(userId, MatchStatus.DETACHED_PENDING_DELETE))
        .willReturn(Optional.of(detachedMember));

    // when: 서비스 메서드를 실행하면
    DetachedMatchStatusResponse response = sut.getDetachedMatchStatus(userId);

    // then: hasDetachedMatch는 true이고, matchId가 정확히 반환되어야 한다
    assertThat(response.isHasDetachedMatch()).isTrue();
    assertThat(response.getMatchId()).isEqualTo(matchId);
  }

  @Test
  @DisplayName("복구 가능 매칭 조회: 복구할 매칭이 없는 경우")
  void getDetachedMatchStatus_whenMatchDoesNotExist() {
    // given: 2번 유저에게는 복구 가능한 매칭이 없는 상황
    Long userId = 2L;
    given(matchMemberRepository.findDetachedMatchForUser(userId, MatchStatus.DETACHED_PENDING_DELETE))
        .willReturn(Optional.empty());

    // when: 서비스 메서드를 실행하면
    DetachedMatchStatusResponse response = sut.getDetachedMatchStatus(userId);

    // then: hasDetachedMatch는 false이고, matchId는 null이어야 한다
    assertThat(response.isHasDetachedMatch()).isFalse();
    assertThat(response.getMatchId()).isNull();
  }
}
