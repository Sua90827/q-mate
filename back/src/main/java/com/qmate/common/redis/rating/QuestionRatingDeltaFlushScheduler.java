package com.qmate.common.redis.rating;

import com.qmate.domain.question.repository.QuestionRepository;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class QuestionRatingDeltaFlushScheduler {

  private final StringRedisTemplate redis;
  private final QuestionRepository questionRepository;

  @Scheduled(
      fixedDelayString = "${rating.flush.delay-ms:1800000}",        // 30분
      initialDelayString = "${rating.flush.initial-delay-ms:120000}" // 2분
  )
  @Transactional
  public void flushDeltas_create() {
    Set<String> dirty = redis.opsForSet().members(QuestionRatingRedisKeys.dirtySet());
    if (dirty == null || dirty.isEmpty()) {
      return;
    }

    Map<Long, long[]> backUp = new HashMap<>();

    try {
      for (String qidStr : dirty) {
        final long qid;
        try {
          qid = Long.parseLong(qidStr);
        } catch (NumberFormatException nfe) {
          redis.opsForSet().remove(QuestionRatingRedisKeys.dirtySet(), qidStr);
          continue;
        }
        long likeDelta = getAndDel(QuestionRatingRedisKeys.likeDelta(qid));
        long dislikeDelta = getAndDel(QuestionRatingRedisKeys.dislikeDelta(qid));
        if (likeDelta == 0 && dislikeDelta == 0) {
          redis.opsForSet().remove(QuestionRatingRedisKeys.dirtySet(), qidStr);
          continue;
        }
        // 백업: 롤백 대비
        backUp.put(qid, new long[]{likeDelta, dislikeDelta});

        questionRepository.findById(qid).ifPresent(q -> q.applyRatingDelta(likeDelta, dislikeDelta));
        log.info("질문 평가 카운팅 업데이트 - qid: {}, likeDelta: {}, dislikeDelta: {}", qid, likeDelta, dislikeDelta);

        // 처리 완료: 더티셋에서 제거
        redis.opsForSet().remove(QuestionRatingRedisKeys.dirtySet(), qidStr);
      }

    } catch (Exception e) {
      log.error("질문 평가 카운팅 업데이트 중 오류 발생하여 롤백 작업 수행", e);
      // 롤백: 델타 복원
      for (Map.Entry<Long, long[]> entry : backUp.entrySet()) {
        long qid = entry.getKey();
        long[] deltas = entry.getValue();
        if (deltas[0] != 0) {
          redis.opsForValue().increment(QuestionRatingRedisKeys.likeDelta(qid), deltas[0]);
        }
        if (deltas[1] != 0) {
          redis.opsForValue().increment(QuestionRatingRedisKeys.dislikeDelta(qid), deltas[1]);
        }
        redis.opsForSet().add(QuestionRatingRedisKeys.dirtySet(), String.valueOf(qid));
        log.info("롤백 완료 - qid: {}, likeDelta: {}, dislikeDelta: {}", qid, deltas[0], deltas[1]);
      }
      throw e;
    }
  }

  private long getAndDel(String key) throws DataAccessException {
    Long val = redis.execute((RedisCallback<Long>) connection -> {
      byte[] k = key.getBytes(StandardCharsets.UTF_8);
      byte[] v = connection.stringCommands().getDel(k);
      if (v == null) {
        return 0L;
      }
      return Long.parseLong(new String(v, StandardCharsets.UTF_8));
    });
    return val == null ? 0L : val;
  }

}
