package com.qmate.domain.quetioninstance.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.qmate.domain.questioninstance.repository.QuestionInstanceRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@Tag("local")
@DataJpaTest
class FindLatestNotifiedIdByMatchTest {

  @TestConfiguration
  static class QuerydslTestConfig {
    @Bean
    JPAQueryFactory jpaQueryFactory(EntityManager em) {
      return new JPAQueryFactory(em);
    }
  }

  @PersistenceContext
  EntityManager em;
  @Autowired
  QuestionInstanceRepository questionInstanceRepository;

  @BeforeEach
  void disableFk() {
    em.createNativeQuery("SET REFERENTIAL_INTEGRITY FALSE").executeUpdate(); // H2 only
  }

  @AfterEach
  void enableFk() {
    em.createNativeQuery("SET REFERENTIAL_INTEGRITY TRUE").executeUpdate();
  }

  @Test
  @DisplayName("match 내 notifiedAt DESC, 동시각은 id DESC로 최신 1건 선택")
  void picks_latest_by_notified_then_id() {
    Long matchId = 1L;

    LocalDateTime t2 = LocalDateTime.now().minusHours(1);

    // 후보 제외 (notified_at = null)
    insertQI(matchId, /*notified*/ null);

    // 동시각 두 건 (tie → PK DESC가 선택되어야 함)
    insertQI(matchId, t2); // 먼저 INSERT → id 낮음
    Long lastId = insertQI(matchId, t2); // 나중 INSERT → id 높음 (기대값)

    em.flush();
    em.clear();

    Optional<Long> found = questionInstanceRepository.findLatestDeliveredIdByMatch(matchId);

    assertThat(found).isPresent();
    assertThat(found.get()).isEqualTo(lastId);
  }

  @Test
  @DisplayName("notifiedAt이 전부 NULL이면 빈 결과 반환")
  void returns_empty_when_all_null() {
    Long matchId = 2L;

    insertQI(matchId, null);
    insertQI(matchId, null);

    em.flush();
    em.clear();

    Optional<Long> found = questionInstanceRepository.findLatestDeliveredIdByMatch(matchId);

    assertThat(found).isEmpty();
  }

  /**
   * question_instance에 최소 컬럼만 native insert (FK 무시)
   * 반환: 생성된 PK (H2 IDENTITY 현재값 조회)
   */
  private Long insertQI(Long matchId, LocalDateTime notifiedAt) {
    // delivered_at, status, created_at, updated_at 정도만 채워주면 됨
    em.createNativeQuery("""
        INSERT INTO question_instance
          (match_id, question_id, custom_question_id,
           delivered_at, notified_at, status, created_at, updated_at)
        VALUES ( ?, NULL, NULL, ?, ?, 'PENDING', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP )
        """)
        .setParameter(1, matchId)
        .setParameter(2, Timestamp.valueOf(LocalDateTime.now()))
        .setParameter(3, notifiedAt == null ? null : Timestamp.valueOf(notifiedAt))
        .executeUpdate();

    // 방금 들어간 PK 가져오기 (H2 전용)
    Long id = ((Number) em.createNativeQuery("SELECT MAX(question_instance_id) FROM question_instance")
        .getSingleResult()).longValue();
    return id;
  }
}
