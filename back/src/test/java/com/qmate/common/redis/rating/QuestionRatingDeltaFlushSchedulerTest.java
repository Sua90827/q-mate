package com.qmate.common.redis.rating;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;

import com.qmate.domain.question.entity.Question;
import com.qmate.domain.question.entity.QuestionCategory;
import com.qmate.domain.question.repository.QuestionCategoryRepository;
import com.qmate.domain.question.repository.QuestionRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

@ActiveProfiles("test")
@Tag("local")
@SpringBootTest
class QuestionRatingDeltaFlushSchedulerTest {

  @Autowired
  StringRedisTemplate redis;
  @MockitoSpyBean
  QuestionRepository questionRepository;
  @Autowired
  QuestionCategoryRepository questionCategoryRepository;

  @Autowired
  QuestionRatingDeltaFlushScheduler scheduler;
  @Autowired
  EntityManager em;

  @BeforeEach
  void cleanBefore() {
    flushDb(redis);
  }

  @AfterEach
  void cleanAfter() {
    flushDb(redis);
  }

  @Test
  void flush_success() {
    // given: 질문 한 건 + 델타 적재
    QuestionCategory category = QuestionCategory.builder().name("카테고리").build();
    questionCategoryRepository.save(category);
    Question saved = questionRepository.save(
        Question.builder().category(category).text("질문").likeCount(0L).dislikeCount(0L).build()
    );
    long qid = saved.getId();

    redis.opsForValue().increment(QuestionRatingRedisKeys.likeDelta(qid), 5);
    redis.opsForSet().add(QuestionRatingRedisKeys.dirtySet(), String.valueOf(qid));

    // when
    scheduler.flushDeltas_create();

    // then: DB 반영 + Redis 정리 확인
    Question q = questionRepository.findById(qid).orElseThrow();
    assertThat(q.getLikeCount()).isEqualTo(5L);
    assertThat(redis.opsForValue().get(QuestionRatingRedisKeys.likeDelta(qid))).isNull();
    assertThat(redis.opsForSet().isMember(QuestionRatingRedisKeys.dirtySet(), String.valueOf(qid))).isFalse();
  }

  @Test
  void flush_fail_then_restore_deltas_and_keep_dirty() {
    // given
    QuestionCategory category = QuestionCategory.builder().name("카테고리").build();
    questionCategoryRepository.save(category);
    Question saved = questionRepository.save(
        Question.builder().category(category).text("질문").likeCount(0L).dislikeCount(0L).build()
    );
    long qid = saved.getId();

    // 델타 적재(혼합 케이스)
    redis.opsForValue().increment(QuestionRatingRedisKeys.likeDelta(qid), 3);
    redis.opsForValue().increment(QuestionRatingRedisKeys.dislikeDelta(qid), 2);
    redis.opsForSet().add(QuestionRatingRedisKeys.dirtySet(), String.valueOf(qid));

    // DB 접근 시 예외를 던지도록 스텁 → 스케줄러가 catch로 진입해야 함
    doThrow(new RuntimeException("DB down")).when(questionRepository).findById(qid);

    // when & then: 예외를 다시 던지는 것이 정상 동작
    assertThrows(RuntimeException.class, () -> scheduler.flushDeltas_create());

    // 복원 검증: Redis 델타가 원래 값으로 되돌아와야 함
    assertThat(redis.opsForValue().get(QuestionRatingRedisKeys.likeDelta(qid))).isEqualTo("3");
    assertThat(redis.opsForValue().get(QuestionRatingRedisKeys.dislikeDelta(qid))).isEqualTo("2");
    assertThat(redis.opsForSet().isMember(QuestionRatingRedisKeys.dirtySet(), String.valueOf(qid))).isTrue();

    // DB는 트랜잭션 롤백 → 카운트 변화 없음
    Question q = em.find(Question.class, qid);
    assertThat(q.getLikeCount()).isEqualTo(0L);
    assertThat(q.getDislikeCount()).isEqualTo(0L);
  }

  private static void flushDb(StringRedisTemplate redis) {
    redis.execute((RedisCallback<Object>) conn -> {
      conn.serverCommands().flushDb();
      return null;
    });
  }
}
