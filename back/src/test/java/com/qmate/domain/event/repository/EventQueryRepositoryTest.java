package com.qmate.domain.event.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.qmate.config.JpaConfig;
import com.qmate.config.QuerydslConfig;
import com.qmate.domain.event.entity.Event;
import com.qmate.domain.event.entity.EventAlarmOption;
import com.qmate.domain.event.entity.EventRepeatType;
import com.qmate.domain.match.Match;
import com.qmate.domain.match.MatchMember;
import com.qmate.domain.match.MatchStatus;
import com.qmate.domain.match.RelationType;
import com.qmate.domain.user.User;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
@Tag("local")
class EventQueryRepositoryTest {

  @Autowired
  private TestEntityManager tem;
  @Autowired
  private EventRepository eventRepository;

  private Long matchId, otherMatchId, userId;
  private Long eNoneBefore, eNoneInside, eNoneAfter;
  private Long eWeeklyPastSeed, eWeeklySeedAfterTo;
  private Long eMonthly31, eYearlyFeb29, otherMatchEvent;

  @BeforeEach
  void setUp() {
    // ===== seed: Match + User(currentMatchId=matchId) + MatchMember =====
    Match match = Match.builder()
        .relationType(RelationType.COUPLE)
        .status(MatchStatus.ACTIVE)
        .startDate(LocalDateTime.now())
        .createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .build();
    tem.persist(match);

    Match other = Match.builder()
        .relationType(RelationType.COUPLE)
        .status(MatchStatus.ACTIVE)
        .startDate(LocalDateTime.now())
        .createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .build();
    tem.persist(other);

    User user = User.builder()
        .email("user99@test.com")
        .nickname("nick")
        .currentMatchId(match.getId())
        .build();
    tem.persist(user);

    MatchMember mm = MatchMember.builder()
        .match(match)
        .user(user)
        .build();
    tem.persist(mm);

    // ===== 동일 매치의 이벤트들 =====
    eNoneBefore = persistEvent(match, "NONE_BEFORE",
        LocalDate.of(2025, 9, 30), EventRepeatType.NONE, false);

    eNoneInside = persistEvent(match, "NONE_INSIDE",
        LocalDate.of(2025, 10, 10), EventRepeatType.NONE, false);

    eNoneAfter = persistEvent(match, "NONE_AFTER",
        LocalDate.of(2025, 11, 1), EventRepeatType.NONE, false);

    eWeeklyPastSeed = persistEvent(match, "WEEKLY_PAST_SEED",
        LocalDate.of(2025, 9, 1), EventRepeatType.WEEKLY, false);

    eWeeklySeedAfterTo = persistEvent(match, "WEEKLY_AFTER_TO",
        LocalDate.of(2025, 12, 1), EventRepeatType.WEEKLY, false);

    eMonthly31 = persistEvent(match, "MONTHLY_31",
        LocalDate.of(2025, 8, 31), EventRepeatType.MONTHLY, false);

    eYearlyFeb29 = persistEvent(match, "YEARLY_FEB29",
        LocalDate.of(2020, 2, 29), EventRepeatType.YEARLY, true);

    // ===== 다른 매치의 이벤트 (스코프 제외 확인용) =====
    otherMatchEvent = persistEvent(other, "OTHER_MATCH_EVENT",
        LocalDate.of(2025, 10, 10), EventRepeatType.NONE, false);

    tem.flush();
    tem.clear();

    matchId = match.getId();
    otherMatchId = other.getId();
    userId = user.getId();
  }

  /* ---------------------------
     권한/스코프 규칙
     --------------------------- */
  @Nested
  @DisplayName("권한/스코프")
  class AuthScope {

    @Test
    @DisplayName("currentMatchId == matchId & 매치 멤버 → 후보 조회 OK")
    void authorized_user_sees_candidates() {
      List<Event> found = eventRepository.findCandidates(
          matchId, userId,
          LocalDate.of(2025,10,1), LocalDate.of(2025,10,31),
          null, null
      );
      assertThat(found).extracting(Event::getId)
          .contains(eNoneInside, eWeeklyPastSeed, eMonthly31, eYearlyFeb29)
          .doesNotContain(otherMatchEvent); // 스코프 외
    }

    @Test
    @DisplayName("currentMatchId 불일치면 0건")
    void current_match_mismatch_returns_empty() {
      User u = tem.find(User.class, userId);
      u.setCurrentMatchId(otherMatchId);
      tem.flush();
      tem.clear();

      List<Event> found = eventRepository.findCandidates(
          matchId, userId,
          LocalDate.of(2025,10,1), LocalDate.of(2025,10,31),
          null, null
      );
      assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("다른 매치의 이벤트는 제외")
    void other_match_events_excluded() {
      List<Event> found = eventRepository.findCandidates(
          matchId, userId,
          LocalDate.of(2025,10,1), LocalDate.of(2025,10,31),
          null, null
      );
      assertThat(found).extracting(Event::getId)
          .doesNotContain(otherMatchEvent);
    }
  }

  /* ---------------------------
     날짜 후보 규칙 (레포 단계)
     --------------------------- */
  @Nested
  @DisplayName("날짜 후보 규칙")
  class DateCandidate {

    @Test
    @DisplayName("NONE: eventAt ∈ [from, to]만 후보 (양끝 포함)")
    void none_between_inclusive() {
      LocalDate from = LocalDate.of(2025, 10, 10);
      LocalDate to   = LocalDate.of(2025, 10, 10);
      List<Event> found = eventRepository.findCandidates(
          matchId, userId, from, to, EventRepeatType.NONE, null
      );
      assertThat(found).extracting(Event::getId)
          .contains(eNoneInside)
          .doesNotContain(eNoneBefore, eNoneAfter);
    }

    @Test
    @DisplayName("반복(W/M/Y): seed(eventAt) ≤ to 인 것만 후보")
    void repeats_seed_must_be_le_to() {
      LocalDate from = LocalDate.of(2025, 10, 1);
      LocalDate to   = LocalDate.of(2025, 10, 31);

      List<Event> weekly = eventRepository.findCandidates(
          matchId, userId, from, to, EventRepeatType.WEEKLY, null
      );
      assertThat(weekly).extracting(Event::getId)
          .contains(eWeeklyPastSeed)
          .doesNotContain(eWeeklySeedAfterTo);

      List<Event> monthly = eventRepository.findCandidates(
          matchId, userId, from, to, EventRepeatType.MONTHLY, null
      );
      assertThat(monthly).extracting(Event::getId)
          .contains(eMonthly31);

      List<Event> yearly = eventRepository.findCandidates(
          matchId, userId, from, to, EventRepeatType.YEARLY, null
      );
      assertThat(yearly).extracting(Event::getId)
          .contains(eYearlyFeb29);
    }
  }

  /* ---------------------------
     필터
     --------------------------- */
  @Nested
  @DisplayName("필터")
  class Filters {

    @Test
    @DisplayName("repeatType 필터 적용")
    void repeatType_filter() {
      List<Event> weekly = eventRepository.findCandidates(
          matchId, userId,
          LocalDate.of(2025,10,1), LocalDate.of(2025,10,31),
          EventRepeatType.WEEKLY, null
      );
      assertThat(weekly).allMatch(e -> e.getRepeatType() == EventRepeatType.WEEKLY);
    }

    @Test
    @DisplayName("anniversary=true 필터 적용")
    void anniversary_true_filter() {
      List<Event> onlyAnniv = eventRepository.findCandidates(
          matchId, userId,
          LocalDate.of(2025,1,1), LocalDate.of(2025,12,31),
          null, true
      );
      assertThat(onlyAnniv).allMatch(Event::isAnniversary);
    }
  }

  private Long persistEvent(Match match, String title, LocalDate seedDate,
      EventRepeatType type, boolean anniversary) {
    Event e = Event.builder()
        .match(match)
        .title(title)
        .description(title)
        .eventAt(seedDate)
        .repeatType(type)
        .alarmOption(EventAlarmOption.WEEK_BEFORE)
        .anniversary(anniversary)
        .createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .build();
    tem.persist(e);
    return e.getId();
  }

}
