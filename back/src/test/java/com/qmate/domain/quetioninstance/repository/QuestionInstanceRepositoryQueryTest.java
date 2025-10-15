package com.qmate.domain.quetioninstance.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.qmate.config.JpaConfig;
import com.qmate.config.QuerydslConfig;
import com.qmate.domain.match.Match;
import com.qmate.domain.match.MatchMember;
import com.qmate.domain.match.MatchStatus;
import com.qmate.domain.match.RelationType;
import com.qmate.domain.question.entity.CustomQuestion;
import com.qmate.domain.questioninstance.entity.QuestionInstance;
import com.qmate.domain.questioninstance.entity.QuestionInstanceStatus;
import com.qmate.domain.questioninstance.repository.QuestionInstanceRepository;
import com.qmate.domain.questioninstance.repository.ReminderTargetRow;
import com.qmate.domain.user.User;
import com.qmate.domain.questioninstance.entity.Answer;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@DataJpaTest
@Import({JpaConfig.class, QuerydslConfig.class})
//@Tag("local")
class QuestionInstanceRepositoryQueryTest {

  @Autowired
  private TestEntityManager tem;
  @Autowired
  private QuestionInstanceRepository repository;

  private Match matchActive;

  private User userPushOn;   // pushEnabled = true
  private User userPushOff;  // pushEnabled = false

  private CustomQuestion cqMatchAcive;

  @BeforeEach
  void setUp() {
    // 매치 2개
    matchActive = tem.persist(Match.builder()
        .relationType(RelationType.COUPLE)
        .status(MatchStatus.ACTIVE)
        .startDate(LocalDateTime.now())
        .createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .build());

    // 유저 2명 (푸시 on/off)
    userPushOn = tem.persist(User.builder()
        .email("pushon@test.com")
        .nickname("on")
        .pushEnabled(true)
        .currentMatchId(matchActive.getId())
        .build());

    userPushOff = tem.persist(User.builder()
        .email("pushoff@test.com")
        .nickname("off")
        .pushEnabled(false)
        .currentMatchId(matchActive.getId())
        .build());

    cqMatchAcive = tem.persist(CustomQuestion.builder()
        .match(matchActive)
        .createdBy(userPushOn.getId())
        .text("CQ for Active")
        .build());

    // 매치 멤버 등록
    tem.persist(MatchMember.builder().match(matchActive).user(userPushOn).build());
    tem.persist(MatchMember.builder().match(matchActive).user(userPushOff).build());

    tem.flush();
    tem.clear();
  }

  @Nested
  @DisplayName("findReminderTargetsBetween")
  class FindReminderTargetsBetween {

    @Test
    @DisplayName("윈도우[11:00,11:50) & PENDING & ACTIVE 매치 & 미답변만 반환, pushEnabled 값까지 매핑")
    void returnsOnlyUnansweredUsersWithinWindow() {
      // given: 2025-01-10 11:00 ~ 11:50
      LocalDateTime start = LocalDateTime.of(2025, Month.JANUARY, 10, 11, 0, 0);
      LocalDateTime end = start.plusMinutes(50);

      // QI #1: 대상 — PENDING, delivered=11:30, match=ACTIVE
      QuestionInstance qiIn = tem.persist(QuestionInstance.builder()
          .match(matchActive)
          .customQuestion(cqMatchAcive)
          .status(QuestionInstanceStatus.PENDING)
          .deliveredAt(LocalDateTime.of(2025, 1, 10, 11, 30, 0))
          .build());

      // QI #2: 윈도우 밖 — 11:55
      QuestionInstance qiOutOfWindow = tem.persist(QuestionInstance.builder()
          .match(matchActive)
          .customQuestion(cqMatchAcive)
          .status(QuestionInstanceStatus.PENDING)
          .deliveredAt(LocalDateTime.of(2025, 1, 10, 11, 55, 0))
          .build());

      // 같은 QI #1에 대해 userPushOff는 이미 답변 → 제외되어야 함
      tem.persist(Answer.builder()
          .questionInstance(qiIn)
          .userId(userPushOff.getId())
          .content("done") // 실제 필드명/필수 값에 맞게 수정
          .submittedAt(LocalDateTime.now())
          .build());

      tem.flush();
      tem.clear();

      // when
      List<ReminderTargetRow> rows = repository.findReminderTargetsBetween(start, end);

      // then: QI #1 × userPushOn 만 반환 (미답변 + pushEnabled=true)
      assertThat(rows)
          .hasSize(1)
          .allSatisfy(r -> {
            assertThat(r.getQiId()).isEqualTo(qiIn.getId());
            assertThat(r.getMatchId()).isEqualTo(matchActive.getId());
            assertThat(r.getUserId()).isEqualTo(userPushOn.getId());
            assertThat(r.getPushEnabled()).isTrue();
          });
    }

    @Test
    @DisplayName("PENDING 아님 / ACTIVE 아님 / 윈도우 밖 / 다른 매치 → 모두 제외")
    void excludesNonPendingNonActiveOutOfWindowOtherMatch() {
      LocalDateTime start = LocalDateTime.of(2025, Month.JANUARY, 10, 11, 0, 0);
      LocalDateTime end = start.plusMinutes(50);

      // (제외) COMPLETED 상태
      tem.persist(QuestionInstance.builder()
          .match(matchActive)
          .customQuestion(cqMatchAcive)
          .status(QuestionInstanceStatus.COMPLETED)
          .deliveredAt(LocalDateTime.of(2025, 1, 10, 11, 10, 0))
          .build());

      // (제외) 윈도우 경계 end(11:50) — 반개구간 밖
      tem.persist(QuestionInstance.builder()
          .match(matchActive)
          .customQuestion(cqMatchAcive)
          .status(QuestionInstanceStatus.PENDING)
          .deliveredAt(LocalDateTime.of(2025, 1, 10, 11, 50, 0))
          .build());

      tem.flush();
      tem.clear();

      List<ReminderTargetRow> rows = repository.findReminderTargetsBetween(start, end);

      assertThat(rows).isEmpty();
    }
  }
}
