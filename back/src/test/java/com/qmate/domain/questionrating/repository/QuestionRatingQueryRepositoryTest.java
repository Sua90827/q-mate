package com.qmate.domain.questionrating.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.qmate.config.JpaConfig;
import com.qmate.domain.match.Match;
import com.qmate.domain.match.MatchMember;
import com.qmate.domain.question.entity.Question;
import com.qmate.domain.question.entity.QuestionCategory;
import com.qmate.domain.questioninstance.entity.QuestionInstance;
import com.qmate.domain.questionrating.entity.QuestionRating;
import com.qmate.domain.questionrating.model.response.CategoryLikeStat;
import com.qmate.domain.user.User;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@ActiveProfiles("test")
@Tag("local")
@DataJpaTest
@Transactional
@Import(JpaConfig.class)
class QuestionRatingQueryRepositoryTest {

  @TestConfiguration
  static class QueryDslTestConfig {
    @Bean
    JPAQueryFactory jpaQueryFactory(EntityManager em) {
      return new JPAQueryFactory(em);
    }
  }

  @Autowired EntityManager em;
  @Autowired QuestionRatingRepository repository;

  // 테스트 공통 기간: anchor = 2025-09-30 → 전월 2025-08-01 ~ 2025-08-31 23:59:59
  private final LocalDateTime FROM = YearMonth.of(2025, 8).atDay(1).atStartOfDay();
  private final LocalDateTime TO   = YearMonth.of(2025, 8).atEndOfMonth().atTime(23,59,59);

  @Test
  @DisplayName("기본: 전월 노출 + 우리 매치 구성원 LIKE만 카테고리별 집계")
  void basic_count_by_category() {
    // 매치/유저
    Match match = persist(Match.builder().build());
    User me = persist(User.builder().currentMatchId(match.getId()).build());
    MatchMember meMm = persist(MatchMember.builder().match(match).user(me).build());

    // 다른 매치/유저
    Match otherMatch = persist(Match.builder().build());
    User otherUser = persist(User.builder().currentMatchId(otherMatch.getId()).build());
    persist(MatchMember.builder().match(otherMatch).user(otherUser).build());

    // 카테고리 2개
    QuestionCategory c1 = persist(QuestionCategory.builder().name("일상").build());
    QuestionCategory c2 = persist(QuestionCategory.builder().name("가치관").build());

    // 관리자 질문 3개 (c1: q1,q2 / c2: q3)
    Question q1 = persist(Question.builder().category(c1).build());
    Question q2 = persist(Question.builder().category(c1).build());
    Question q3 = persist(Question.builder().category(c2).build());

    // 전월 우리 매치에 노출된 QI (q1, q2, q3)
    persist(QuestionInstance.builder().match(match).question(q1).deliveredAt(FROM.plusDays(3)).build());
    persist(QuestionInstance.builder().match(match).question(q2).deliveredAt(FROM.plusDays(5)).build());
    persist(QuestionInstance.builder().match(match).question(q3).deliveredAt(FROM.plusDays(10)).build());

    // 현재월(전월 아님) 노출된 QI (집계 제외)
    persist(QuestionInstance.builder().match(match).question(q1).deliveredAt(TO.plusDays(2)).build());

    // LIKE (우리 매치 구성원: me)
    persist(QuestionRating.builder().question(q1).userId(me.getId()).isLike(true).build());  // c1 +1
    persist(QuestionRating.builder().question(q2).userId(me.getId()).isLike(true).build());  // c1 +1
    persist(QuestionRating.builder().question(q3).userId(me.getId()).isLike(true).build());  // c2 +1

    // 다른 매치 유저의 LIKE (집계 제외)
    persist(QuestionRating.builder().question(q1).userId(otherUser.getId()).isLike(true).build());

    em.flush(); em.clear();

    List<CategoryLikeStat> result = repository.findMonthlyLikesByCategory(me.getId(), match.getId(), FROM, TO);

    // 정렬: likeCount desc, name asc
    assertThat(result).hasSize(2);
    assertThat(result.get(0).getCategoryName()).isEqualTo("일상");
    assertThat(result.get(0).getLikeCount()).isEqualTo(2L);
    assertThat(result.get(1).getCategoryName()).isEqualTo("가치관");
    assertThat(result.get(1).getLikeCount()).isEqualTo(1L);
  }

  @Test
  @DisplayName("중복 노출 방지: 같은 질문이 전월에 여러 번 노출되어도 LIKE는 1회만 집계")
  void deduplicate_likes_even_if_multiple_instances() {
    Match match = persist(Match.builder().build());
    User me = persist(User.builder().currentMatchId(match.getId()).build());
    persist(MatchMember.builder().match(match).user(me).build());

    QuestionCategory c = persist(QuestionCategory.builder().name("대화/소통").build());
    Question q = persist(Question.builder().category(c).build());

    // 전월에 같은 질문 q가 여러 번 노출
    persist(QuestionInstance.builder().match(match).question(q).deliveredAt(FROM.plusDays(1)).build());
    persist(QuestionInstance.builder().match(match).question(q).deliveredAt(FROM.plusDays(2)).build());
    persist(QuestionInstance.builder().match(match).question(q).deliveredAt(FROM.plusDays(3)).build());

    // me가 q에 LIKE 1회
    persist(QuestionRating.builder().question(q).userId(me.getId()).isLike(true).build());

    em.flush(); em.clear();

    List<CategoryLikeStat> result = repository.findMonthlyLikesByCategory(me.getId(), match.getId(), FROM, TO);
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getCategoryName()).isEqualTo("대화/소통");
    assertThat(result.get(0).getLikeCount()).isEqualTo(1L);
  }

  @Test
  @DisplayName("권한 검증: user.currentMatchId != matchId 이면 집계되지 않음(빈 결과)")
  void forbidden_when_user_not_in_match() {
    Match match = persist(Match.builder().build());
    // me는 currentMatchId가 null 또는 다른 매치
    User me = persist(User.builder().currentMatchId(null).build());

    QuestionCategory c = persist(QuestionCategory.builder().name("일상").build());
    Question q = persist(Question.builder().category(c).build());
    persist(QuestionInstance.builder().match(match).question(q).deliveredAt(FROM.plusDays(1)).build());
    // me가 LIKE 눌러도(비정상 케이스라 가정) 권한 조건에서 걸러짐
    persist(QuestionRating.builder().question(q).userId(me.getId()).isLike(true).build());

    em.flush(); em.clear();

    List<CategoryLikeStat> result = repository.findMonthlyLikesByCategory(me.getId(), match.getId(), FROM, TO);
    assertThat(result).isEmpty(); // 레포 쿼리 where절의 권한 서브쿼리에 의해 필터됨
  }

  @Test
  @DisplayName("타 매치 유저의 LIKE는 제외되어야 한다")
  void exclude_likes_from_other_match_members() {
    Match match = persist(Match.builder().build());
    User me = persist(User.builder().currentMatchId(match.getId()).build());
    persist(MatchMember.builder().match(match).user(me).build());

    Match otherMatch = persist(Match.builder().build());
    User other = persist(User.builder().currentMatchId(otherMatch.getId()).build());
    persist(MatchMember.builder().match(otherMatch).user(other).build());

    QuestionCategory c = persist(QuestionCategory.builder().name("가치관").build());
    Question q = persist(Question.builder().category(c).build());
    persist(QuestionInstance.builder().match(match).question(q).deliveredAt(FROM.plusDays(4)).build());

    // 다른 매치 유저의 LIKE → 제외
    persist(QuestionRating.builder().question(q).userId(other.getId()).isLike(true).build());

    // 우리 매치 유저의 LIKE → 포함
    persist(QuestionRating.builder().question(q).userId(me.getId()).isLike(true).build());

    em.flush(); em.clear();

    List<CategoryLikeStat> result = repository.findMonthlyLikesByCategory(me.getId(), match.getId(), FROM, TO);
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getCategoryName()).isEqualTo("가치관");
    assertThat(result.get(0).getLikeCount()).isEqualTo(1L);
  }

  // ---------- helpers ----------
  private <T> T persist(T entity) {
    if (entity instanceof User u) {
      u.setEmail("testuser+" + System.nanoTime() + "@example.com"); // unique
      u.setNickname("테스터" + System.nanoTime());
      u.setBirthDate(LocalDateTime.now().minusYears(20).toLocalDate());
    } else if (entity instanceof Question q) {
      q.setText("질문 내용 " + System.nanoTime());
    }
    em.persist(entity);
    return entity;
  }
}
