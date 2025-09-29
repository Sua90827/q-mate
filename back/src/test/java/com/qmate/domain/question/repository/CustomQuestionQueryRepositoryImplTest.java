package com.qmate.domain.question.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.qmate.config.JpaConfig;
import com.qmate.domain.match.Match;
import com.qmate.domain.match.RelationType;
import com.qmate.domain.question.entity.CustomQuestion;
import com.qmate.domain.question.model.request.CustomQuestionStatusFilter;
import com.qmate.domain.question.model.response.CustomQuestionResponse;
import com.qmate.domain.questioninstance.entity.QuestionInstance;
import com.qmate.domain.questioninstance.entity.QuestionInstanceStatus;
import com.qmate.exception.custom.question.CustomQuestionInvalidSortKeyException;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@DataJpaTest
@Import(JpaConfig.class)
@Tag("local")
class CustomQuestionQueryRepositoryImplTest {

  static final Long USER_ID = 100L;

  @Autowired
  EntityManager em;
  @Autowired
  CustomQuestionRepository repo;

  @TestConfiguration
  static class QuerydslTestConfig {

    @Bean
    JPAQueryFactory jpaQueryFactory(EntityManager em) {
      return new JPAQueryFactory(em);
    }
  }

  private Match persistMatch(RelationType type) {
    Match m = Match.builder()
        .relationType(type)
        .build();
    em.persist(m);
    return m;
  }

  private CustomQuestion persistCQ(Match m, String text, Long createdBy) {
    CustomQuestion cq = CustomQuestion.builder()
        .match(m)
        .text(text)
        .createdBy(createdBy)
        .build();
    em.persist(cq);
    return cq;
  }

  private QuestionInstance persistQI(CustomQuestion cq, QuestionInstanceStatus st) {
    QuestionInstance qi = QuestionInstance.builder()
        .customQuestion(cq)
        .match(cq.getMatch())
        .status(st)
        .deliveredAt(LocalDateTime.now())
        .build();
    em.persist(qi);
    return qi;
  }

  @Test
  @DisplayName("EDITABLE: 해당 CQ에 매칭된 QI가 없으면 조회")
  void editable_only() {
    var m = persistMatch(RelationType.FRIEND);
    var cq1 = persistCQ(m, "no qi", USER_ID);   // EDITABLE
    var cq2 = persistCQ(m, "has qi", USER_ID);  // not editable
    persistQI(cq2, QuestionInstanceStatus.PENDING);

    Page<CustomQuestionResponse> page = repo.findPageByOwnerAndStatusFilter(
        USER_ID, m.getId(), CustomQuestionStatusFilter.EDITABLE,
        PageRequest.of(0, 20, Sort.by(Sort.Order.desc("createdAt"))));

    assertThat(page.getContent()).extracting(CustomQuestionResponse::getCustomQuestionId)
        .contains(cq1.getId()).doesNotContain(cq2.getId());
    assertThat(page.getContent()).allMatch(CustomQuestionResponse::isEditable);
  }

  @Test
  @DisplayName("PENDING / COMPLETED 상태 필터링")
  void pending_completed() {
    var m = persistMatch(RelationType.COUPLE);
    var cqP = persistCQ(m, "pending", USER_ID);
    var cqC = persistCQ(m, "completed", USER_ID);
    persistQI(cqP, QuestionInstanceStatus.PENDING);
    persistQI(cqC, QuestionInstanceStatus.COMPLETED);

    Page<CustomQuestionResponse> p1 = repo.findPageByOwnerAndStatusFilter(
        USER_ID, m.getId(), CustomQuestionStatusFilter.PENDING, PageRequest.of(0, 10));
    Page<CustomQuestionResponse> p2 = repo.findPageByOwnerAndStatusFilter(
        USER_ID, m.getId(), CustomQuestionStatusFilter.COMPLETED, PageRequest.of(0, 10));

    assertThat(p1.getContent()).extracting(CustomQuestionResponse::getCustomQuestionId).containsExactly(cqP.getId());
    assertThat(p2.getContent()).extracting(CustomQuestionResponse::getCustomQuestionId).containsExactly(cqC.getId());
  }

  @Test
  @DisplayName("작성자(userId) 및 matchId 범위 이외 데이터는 제외")
  void owner_and_match_scope() {
    var m1 = persistMatch(RelationType.FRIEND);
    var m2 = persistMatch(RelationType.COUPLE);
    var cq1 = persistCQ(m1, "mine in m1", USER_ID);
    var cq2 = persistCQ(m1, "others in m1", 999L);
    var cq3 = persistCQ(m2, "mine in m2", USER_ID);

    Page<CustomQuestionResponse> page = repo.findPageByOwnerAndStatusFilter(
        USER_ID, m1.getId(), null, PageRequest.of(0, 20));

    assertThat(page.getContent()).extracting(CustomQuestionResponse::getCustomQuestionId)
        .contains(cq1.getId())
        .doesNotContain(cq2.getId(), cq3.getId());
  }

  @Test
  @DisplayName("정렬 키가 잘못되면 커스텀 예외 발생")
  void invalid_sort_key() {
    var m = persistMatch(RelationType.FRIEND);
    persistCQ(m, "x", USER_ID);

    assertThatThrownBy(() ->
        repo.findPageByOwnerAndStatusFilter(USER_ID, m.getId(), null,
            PageRequest.of(0, 10, Sort.by(Sort.Order.desc("unknown")))))
        .isInstanceOf(CustomQuestionInvalidSortKeyException.class);
  }
}
