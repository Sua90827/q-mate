package com.qmate.common.redis.rating;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisQuestionRatingCounter {

  private final StringRedisTemplate redis;

  public void addDelta(long questionId, boolean isLike) {
    if (isLike) {
      redis.opsForValue().increment(QuestionRatingRedisKeys.likeDelta(questionId));
    } else {
      redis.opsForValue().increment(QuestionRatingRedisKeys.dislikeDelta(questionId));
    }
    redis.opsForSet().add(QuestionRatingRedisKeys.dirtySet(), String.valueOf(questionId));
  }
}
