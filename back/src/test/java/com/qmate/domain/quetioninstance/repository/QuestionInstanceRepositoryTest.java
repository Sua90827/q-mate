package com.qmate.domain.quetioninstance.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.qmate.config.JpaConfig;
import com.qmate.config.QuerydslConfig;
import com.qmate.domain.match.Match;
import com.qmate.domain.match.MatchMember;
import com.qmate.domain.match.MatchSetting;
import com.qmate.domain.match.MatchStatus;
import com.qmate.domain.question.entity.CustomQuestion;
import com.qmate.domain.questioninstance.entity.QuestionInstance;
import com.qmate.domain.questioninstance.entity.QuestionInstanceStatus;
import com.qmate.domain.questioninstance.repository.QuestionInstanceRepository;
import com.qmate.domain.user.User;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
@Import({JpaConfig.class, QuerydslConfig.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
//@Tag("local")
class QuestionInstanceRepositoryTest {

  @Autowired
  private EntityManager em;
  @Autowired
  private QuestionInstanceRepository qiRepository;

  @Test
  @DisplayName("fetch join: 정각 대상 QI 조회 시 match/members/user까지 로딩되어 Lazy 문제 없이 접근 가능")
  void findPendingToDeliverForHourWithMembersAndUser_fetchJoin_ok() {
    // given: Match + Setting + Members + Users + QI (deliveredAt=null, PENDING)

    Match match = Match.builder()
        .status(MatchStatus.ACTIVE)
        .relationType(com.qmate.domain.match.RelationType.COUPLE)
        .startDate(LocalDateTime.now().minusDays(100))
        .build();
    em.persist(match);

    MatchSetting ms = new MatchSetting(match);
    em.persist(ms);

    User u1 = User.builder()
        .email("test1@test.com")
        .nickname("test1")
        .pushEnabled(true)
        .build();
    em.persist(u1);

    User u2 = User.builder()
        .email("test2@test.com")
        .nickname("test2")
        .pushEnabled(false)
        .build();
    em.persist(u2);

    MatchMember mm1 = MatchMember.builder()
        .match(match)
        .user(u1)
        .build();
    em.persist(mm1);

    MatchMember mm2 = MatchMember.builder()
        .match(match)
        .user(u2)
        .build();
    em.persist(mm2);

    CustomQuestion customQuestion = CustomQuestion.builder()
        .match(match)
        .createdBy(u1.getId())
        .text("test")
        .build();
    em.persist(customQuestion);

    QuestionInstance qi = QuestionInstance.builder()
        .match(match)
        .status(QuestionInstanceStatus.PENDING)
        .deliveredAt(null)
        .customQuestion(customQuestion)
        .createdAt(LocalDateTime.now())
        .build();
    em.persist(qi);

    em.flush();
    em.clear();

    // when
    List<QuestionInstance> rows = qiRepository.findPendingToDeliverForHourWithMembersAndUser(
        12, MatchStatus.ACTIVE, QuestionInstanceStatus.PENDING);

    // then
    assertThat(rows).hasSize(1);
    QuestionInstance found = rows.get(0);

    // fetch join 확인
    var members = found.getMatch().getMembers();
    assertThat(members).hasSize(2);
    assertThat(members.get(0).getUser().getId()).isNotNull();
    assertThat(members.get(1).getUser().getId()).isNotNull();
  }
}
