package com.qmate.common.redis;

import com.qmate.common.constants.redis.RedisKeyConstants;
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

  //==================초대 시도 횟수 제한=======================//
  private static final String ATTEMPT_COUNT_PREFIX = RedisKeyConstants.ATTEMPT_COUNT_PREFIX;
  private static final String LOCK_PREFIX = RedisKeyConstants.LOCK_PREFIX;

  //시도 횟수 1 증가시키기
  public long incrementAttemptCount(Long userId){
    String key = ATTEMPT_COUNT_PREFIX + userId;
    return redisTemplate.opsForValue().increment(key);
  }
  //잠금 상태인지 확인
  public boolean isLocked(Long userId){
    String key = LOCK_PREFIX + userId;
    return Boolean.TRUE.equals(redisTemplate.hasKey(key));
  }
  //24시간 잠금 설정하기
  public void lockUser(Long userId){
    String countKey = ATTEMPT_COUNT_PREFIX + userId;
    String lockKey = LOCK_PREFIX + userId;

    redisTemplate.opsForValue().set(lockKey, "locked", Duration.ofHours(24));
    redisTemplate.delete(countKey);//24시간 락 이후 카운트는 삭제
  }
  /**
   * 사용자의 잠금 상태에 대한 남은 유효시간을 초(second) 단위로 반환합니다.
   * @param userId 조회할 사용자 ID
   * @return 남은 시간 (초). 키가 없으면 Optional.empty() 반환
   */
  public Optional<Long> getLockTimeRemaining(Long userId) {
    String key = RedisKeyConstants.LOCK_PREFIX + userId;
    // redisTemplate.getExpire()는 키의 남은 TTL을 초 단위로 반환합니다.
    // 키가 없거나 TTL이 설정되지 않은 경우 null을 반환할 수 있습니다.
    return Optional.ofNullable(redisTemplate.getExpire(key));
  }
}