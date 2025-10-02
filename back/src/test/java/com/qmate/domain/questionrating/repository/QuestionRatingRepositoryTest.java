package com.qmate.domain.questionrating.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.qmate.config.JpaConfig;
import com.qmate.domain.question.entity.Question;
import com.qmate.domain.questionrating.entity.QuestionRating;
import com.qmate.security.UserPrincipal;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@Tag("local")
@DataJpaTest
@Import(JpaConfig.class) // @EnableJpaAuditing(auditorAwareRef = "auditorAware")
class QuestionRatingRepositoryTest {

  @TestConfiguration
  static class QuerydslTestConfig {
    @Bean
    JPAQueryFactory jpaQueryFactory(EntityManager em) {
      return new JPAQueryFactory(em);
    }
  }

  @Autowired
  QuestionRatingRepository repository;
  @Autowired
  EntityManager em;

  private static final long TEST_USER_ID = 99L;

  @BeforeEach
  void setUpSecurityContext() {
    UserPrincipal principal = new UserPrincipal(TEST_USER_ID, "tester@example.com", "ROLE_USER");
    Authentication auth = new UsernamePasswordAuthenticationToken(principal, null, List.of());
    SecurityContextHolder.getContext().setAuthentication(auth);
  }

  @AfterEach
  void clearSecurityContext() {
    SecurityContextHolder.clearContext();
  }

  @BeforeEach
  void disableFk() {
    em.createNativeQuery("SET REFERENTIAL_INTEGRITY FALSE").executeUpdate(); // H2 only
  }

  @AfterEach
  void enableFk() {
    em.createNativeQuery("SET REFERENTIAL_INTEGRITY TRUE").executeUpdate();
  }

  @Test
  @DisplayName("@CreatedBy 로 user_id 자동 주입 확인")
  void createdBy_setsUserId_onPersist() {
    // 사전조건: question 테이블에 ID=1 인 레코드 1건 존재
    Question questionRef = em.getReference(Question.class, 1L);

    QuestionRating rating = new QuestionRating();
    rating.setQuestion(questionRef);
    rating.setLike(true); // is_like = 1

    QuestionRating saved = repository.saveAndFlush(rating);

    assertThat(saved.getId()).isNotNull();
    assertThat(saved.getUserId()).isEqualTo(TEST_USER_ID);
    assertThat(saved.getCreatedAt()).isNotNull();
    assertThat(saved.isLike()).isTrue();
  }
}
