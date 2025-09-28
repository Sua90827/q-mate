package com.qmate.common.redis.rating;

import lombok.NoArgsConstructor;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public final class QuestionRatingRedisKeys {
  public static String likeDelta(long qid)    { return "q:%d:like_delta".formatted(qid); }
  public static String dislikeDelta(long qid) { return "q:%d:dislike_delta".formatted(qid); }
  public static String dirtySet()             { return "q:dirty"; }
}
