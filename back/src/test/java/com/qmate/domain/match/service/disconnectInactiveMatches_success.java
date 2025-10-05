package com.qmate.domain.match.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.qmate.domain.match.Match;
import com.qmate.domain.match.MatchMember;
import com.qmate.domain.match.MatchStatus;
import com.qmate.domain.match.repository.MatchRepository;
import com.qmate.domain.user.User;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MatchServiceSchedulerTest {

  @InjectMocks
  private MatchService sut;

  @Mock
  private MatchRepository matchRepository;

  @Test
  @DisplayName("자동 연결 끊기: 비활성 매칭 목록을 받아 상태를 올바르게 변경한다")
  void disconnectInactiveMatches_success() {
    // given: 1번(비활성) 매칭이 있는 상황
    Match inactiveMatch = spy(Match.builder().id(1L).status(MatchStatus.ACTIVE).build());

    // ▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼ 이 부분을 수정합니다 ▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼
    // User 객체도 spy()로 감싸서 행동을 추적할 수 있도록 만듭니다.
    User user1 = spy(User.builder().id(1L).build());
    // ▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲

    inactiveMatch.addMember(MatchMember.create(user1, inactiveMatch));

    given(matchRepository.findInactiveMatches(any(LocalDateTime.class)))
        .willReturn(List.of(inactiveMatch));

    // when: 서비스 메서드를 실행하면
    sut.disconnectInactiveMatches();

    // then:
    // 1. 비활성 매칭(inactiveMatch)의 disconnect() 메서드가 1번 호출되었는지 검증
    verify(inactiveMatch, times(1)).disconnect();
    // 2. 비활성 매칭의 멤버(user1)의 leaveMatch() 메서드가 1번 호출되었는지 검증
    verify(user1, times(1)).leaveMatch(); // ◀◀◀ 이제 이 검증이 성공합니다!
  }
}