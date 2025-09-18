package com.qmate.common.redis;

import com.qmate.common.constants.RedisKeyConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@Component
@RequiredArgsConstructor
public class RedisHelper {

  private final StringRedisTemplate redisTemplate;

  private static final String INVITE_CODE_PREFIX = RedisKeyConstants.INVITE_CODE_PREFIX;
  private static final Duration INVITE_CODE_TTL = Duration.ofHours(12);

  // 6자리 랜덤 초대 코드를 생성합니다.
  public String generateRandomCode() {
    return String.format("%06d", ThreadLocalRandom.current().nextInt(0, 1000000));
  }

  /**
   * Redis에 초대 코드를 저장합니다. 키가 이미 존재할 경우 실패합니다.
   *
   * @param code    생성된 6자리 코드
   * @param matchId 매칭 ID
   * @return 저장 성공 여부 (true: 저장 성공, false: 키 존재)
   */
  public boolean setInviteCode(String code, Long matchId) {
    String key = INVITE_CODE_PREFIX + code;
    Boolean isSuccess = redisTemplate.opsForValue()
        .setIfAbsent(key, String.valueOf(matchId), INVITE_CODE_TTL);
    return isSuccess != null && isSuccess;
  }

  /**
   * Redis에서 초대 코드로 매칭 ID를 조회합니다.
   *
   * @param code 조회할 6자리 코드
   * @return 매칭 ID (Optional로 반환)
   */
  public Optional<Long> getMatchIdByInviteCode(String code) {
    String key = INVITE_CODE_PREFIX + code;
    String matchId = redisTemplate.opsForValue().get(key);
    return Optional.ofNullable(matchId).map(Long::valueOf);
  }

  /**
   * 사용된 초대 코드를 Redis에서 삭제합니다.
   *
   * @param code 삭제할 6자리 코드
   */
  public void deleteInviteCode(String code) {
    String key = INVITE_CODE_PREFIX + code;
    redisTemplate.delete(key);
  }
}