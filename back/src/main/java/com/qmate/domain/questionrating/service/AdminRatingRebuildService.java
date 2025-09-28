package com.qmate.domain.questionrating.service;

import com.qmate.common.redis.rating.QuestionRatingRedisKeys;
import com.qmate.domain.question.entity.Question;
import com.qmate.domain.question.repository.QuestionRepository;
import com.qmate.domain.questionrating.repository.QuestionRatingRepository;
import com.qmate.domain.questionrating.repository.projection.RatingAgg;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminRatingRebuildService {

  private final StringRedisTemplate redis;
  private final QuestionRepository questionRepository;
  private final QuestionRatingRepository questionRatingRepository;

  /**
   * 1) Redis의 rating 관련 키 전부 삭제
   * 2) DB의 question_rating에서 전수 집계
   * 3) 모든 Question like/dislike 카운트 최신화(없으면 0)
   */
  @Transactional
  public void rebuildAll() {
    clearRedisRatingKeys();

    // DB 집계
    List<RatingAgg> rows = questionRatingRepository.aggregateAll();
    Map<Long, long[]> aggMap = new HashMap<>(rows.size());
    for (RatingAgg r : rows) {
      aggMap.put(r.getQuestionId(), new long[]{
          Optional.ofNullable(r.getLikeCount()).orElse(0L),
          Optional.ofNullable(r.getDislikeCount()).orElse(0L)
      });
    }

    // 모든 질문을 순회하며 세팅(없으면 0)
    for (Question q : questionRepository.findAll()) {
      long[] v = aggMap.getOrDefault(q.getId(), new long[]{0L, 0L});
      q.setLikeCount(v[0]);
      q.setDislikeCount(v[1]);
    }
  }

  /**
   * Redis rating 관련 키만 안전하게 스캔하여 삭제
   */
  void clearRedisRatingKeys() throws DataAccessException {
    final String likePattern = QuestionRatingRedisKeys.likeDelta(0).replace("0", "*");
    final String dislikePattern = QuestionRatingRedisKeys.dislikeDelta(0).replace("0", "*");
    final String dirtyKey = QuestionRatingRedisKeys.dirtySet();

    Set<String> likes = redis.keys(likePattern);
    if (likes != null && !likes.isEmpty()) {
      redis.delete(likes);
    }

    Set<String> dislikes = redis.keys(dislikePattern);
    if (dislikes != null && !dislikes.isEmpty()) {
      redis.delete(dislikes);
    }

    redis.delete(dirtyKey);
  }

}
