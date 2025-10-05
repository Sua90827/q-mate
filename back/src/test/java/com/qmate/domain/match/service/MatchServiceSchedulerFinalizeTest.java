package com.qmate.domain.match.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.qmate.domain.match.MatchStatus;
import com.qmate.domain.match.Match;
import com.qmate.domain.match.repository.MatchRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MatchServiceSchedulerFinalizeTest {

  @InjectMocks
  private MatchService sut;

  @Mock
  private MatchRepository matchRepository;

  @Test
  @DisplayName("유예기간 만료 매칭 처리: 만료된 매칭 목록을 받아 상태를 올바르게 변경한다")
  void finalizeExpiredMatches_success() {
    // given: 3주가 지나 복구 기간이 만료된 Match 객체를 준비합니다.
    // '진짜' 객체의 메서드 호출을 감시하기 위해 spy로 생성합니다.
    Match expiredMatch = spy(Match.builder()
        .id(1L)
        .status(MatchStatus.DETACHED_PENDING_DELETE)
        .detachedAt(LocalDateTime.now().minusWeeks(3))
        .build());

    // "findMatchesForHardDelete가 호출되면, 위에서 만든 '만료된 매칭' 1개만 담긴 리스트를 돌려줘"
    given(matchRepository.findMatchesForSoftDelete(any(LocalDateTime.class)))
        .willReturn(List.of(expiredMatch));

    // when: 서비스 메서드를 실행하면
    sut.finalizeExpiredMatches();

    // then: 만료된 매칭(expiredMatch)의 markAsDeleted() 메서드가 정확히 1번 호출되었는지 검증
    verify(expiredMatch, times(1)).markAsDeleted();
  }
}