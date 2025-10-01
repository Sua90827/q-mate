package com.qmate.domain.match.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.qmate.common.redis.RedisHelper;
import com.qmate.domain.match.model.response.LockStatusResponse;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class MatchServiceInvitedCodeLockUserTest {

  @InjectMocks
  private MatchService sut;

  @Mock
  private RedisHelper redisHelper;

  @Test
  @DisplayName("잠금 상태 조회: 사용자가 잠겨있는 경우")
  void getLockStatus_whenUserIsLocked() {
    // given: 1번 유저가 잠겨있고, 남은 시간이 3600초인 상황을 가정
    Long userId = 1L;
    Long remainingSeconds = 3600L;
    given(redisHelper.getLockTimeRemaining(userId)).willReturn(Optional.of(remainingSeconds));

    // when: 서비스 메서드를 실행하면
    LockStatusResponse response = sut.getLockStatus(userId);

    // then: isLocked는 true이고, 남은 시간이 정확히 반환되어야 한다
    assertThat(response.isLocked()).isTrue();
    assertThat(response.getRemainingSeconds()).isEqualTo(remainingSeconds);
  }

  @Test
  @DisplayName("잠금 상태 조회: 사용자가 잠겨있지 않은 경우")
  void getLockStatus_whenUserIsNotLocked() {
    // given: 2번 유저가 잠겨있지 않은 상황 (Redis에 키가 없음)
    Long userId = 2L;
    given(redisHelper.getLockTimeRemaining(userId)).willReturn(Optional.empty());

    // when: 서비스 메서드를 실행하면
    LockStatusResponse response = sut.getLockStatus(userId);

    // then: isLocked는 false이고, 남은 시간은 0이어야 한다
    assertThat(response.isLocked()).isFalse();
    assertThat(response.getRemainingSeconds()).isEqualTo(0L);
  }
}
