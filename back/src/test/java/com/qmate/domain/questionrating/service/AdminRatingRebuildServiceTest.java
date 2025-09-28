package com.qmate.domain.questionrating.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.qmate.common.redis.rating.QuestionRatingRedisKeys;
import com.qmate.domain.question.entity.Question;
import com.qmate.domain.question.entity.QuestionCategory;
import com.qmate.domain.question.repository.QuestionCategoryRepository;
import com.qmate.domain.question.repository.QuestionRepository;
import com.qmate.domain.questionrating.entity.QuestionRating;
import com.qmate.domain.questionrating.repository.QuestionRatingRepository;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@Tag("local")
@SpringBootTest
public class AdminRatingRebuildServiceTest {

  @Autowired
  AdminRatingRebuildService service;
  @Autowired
  StringRedisTemplate redis;
  @Autowired
  QuestionRepository questionRepository;
  @Autowired
  QuestionRatingRepository questionRatingRepository;
  @Autowired
  QuestionCategoryRepository questionCategoryRepository;

  @BeforeEach
  void setUp() {
    // DB clean (FK 순서 주의: rating -> question -> category)
    questionRatingRepository.deleteAllInBatch();
    questionRepository.deleteAllInBatch();
    questionCategoryRepository.deleteAllInBatch();
    // Redis clean
    flushDb(redis);
  }

  @AfterEach
  void tearDown() {
    flushDb(redis);
  }

  @Test
  @DisplayName("rebuildAll: DB 전수 집계로 Question 카운트 최신화 + Redis 키 정리")
  void rebuildAll_updates_from_db_and_clears_redis() {
    // given
    QuestionCategory cat = questionCategoryRepository.save(
        QuestionCategory.builder().name("카테고리").build()
    );

    Question q1 = questionRepository.save(
        Question.builder().category(cat).text("Q1").likeCount(999L).dislikeCount(999L).build()
    );
    Question q2 = questionRepository.save(
        Question.builder().category(cat).text("Q2").likeCount(999L).dislikeCount(999L).build()
    );
    Question q3 = questionRepository.save(
        Question.builder().category(cat).text("Q3").likeCount(5L).dislikeCount(7L).build()
    );

    // rating 원장 데이터 (DB가 진실)
    // q1: like 3, dislike 1
    questionRatingRepository.save(QuestionRating.builder().question(q1).userId(1L).isLike(true).build());
    questionRatingRepository.save(QuestionRating.builder().question(q1).userId(2L).isLike(true).build());
    questionRatingRepository.save(QuestionRating.builder().question(q1).userId(3L).isLike(true).build());
    questionRatingRepository.save(QuestionRating.builder().question(q1).userId(4L).isLike(false).build());
    // q2: like 0, dislike 2
    questionRatingRepository.save(QuestionRating.builder().question(q2).userId(5L).isLike(false).build());
    questionRatingRepository.save(QuestionRating.builder().question(q2).userId(6L).isLike(false).build());
    // q3: rating 없음 → 0,0 이어야 함

    // Redis에 남아있던 임의의 델타/dirty (rebuild가 비워야 함)
    redis.opsForValue().increment(QuestionRatingRedisKeys.likeDelta(q1.getId()), 10);
    redis.opsForValue().increment(QuestionRatingRedisKeys.dislikeDelta(q2.getId()), 20);
    redis.opsForSet().add(QuestionRatingRedisKeys.dirtySet(),
        String.valueOf(q1.getId()), String.valueOf(q2.getId()), String.valueOf(q3.getId()));

    // when
    service.rebuildAll();

    // then — DB 최신화 검증
    Question rq1 = questionRepository.findById(q1.getId()).orElseThrow();
    Question rq2 = questionRepository.findById(q2.getId()).orElseThrow();
    Question rq3 = questionRepository.findById(q3.getId()).orElseThrow();

    assertThat(rq1.getLikeCount()).isEqualTo(3L);
    assertThat(rq1.getDislikeCount()).isEqualTo(1L);

    assertThat(rq2.getLikeCount()).isEqualTo(0L);
    assertThat(rq2.getDislikeCount()).isEqualTo(2L);

    assertThat(rq3.getLikeCount()).isEqualTo(0L);
    assertThat(rq3.getDislikeCount()).isEqualTo(0L);

    // Redis 정리 검증
    assertThat(redis.opsForValue().get(QuestionRatingRedisKeys.likeDelta(q1.getId()))).isNull();
    assertThat(redis.opsForValue().get(QuestionRatingRedisKeys.dislikeDelta(q2.getId()))).isNull();
    Set<String> dirty = redis.opsForSet().members(QuestionRatingRedisKeys.dirtySet());
    assertThat(dirty == null || dirty.isEmpty()).isTrue();
  }

  @Test
  @DisplayName("rebuildAll: 여러 번 호출해도 결과는 동일(멱등)")
  void rebuild_is_idempotent() {
    // given
    QuestionCategory cat = questionCategoryRepository.save(
        QuestionCategory.builder().name("카테고리").build()
    );
    Question q = questionRepository.save(
        Question.builder().category(cat).text("Q").likeCount(100L).dislikeCount(100L).build()
    );
    questionRatingRepository.save(QuestionRating.builder().question(q).userId(1L).isLike(true).build());
    questionRatingRepository.save(QuestionRating.builder().question(q).userId(2L).isLike(false).build());
    // when
    service.rebuildAll();
    long like1 = questionRepository.findById(q.getId()).orElseThrow().getLikeCount();
    long dislike1 = questionRepository.findById(q.getId()).orElseThrow().getDislikeCount();
    service.rebuildAll();
    long like2 = questionRepository.findById(q.getId()).orElseThrow().getLikeCount();
    long dislike2 = questionRepository.findById(q.getId()).orElseThrow().getDislikeCount();
    // then
    assertThat(like1).isEqualTo(1L);
    assertThat(dislike1).isEqualTo(1L);
    assertThat(like2).isEqualTo(1L);
    assertThat(dislike2).isEqualTo(1L);
  }

  private static void flushDb(StringRedisTemplate redis) {
    redis.execute((RedisCallback<Object>) conn -> { conn.serverCommands().flushDb(); return null; });
  }
}
