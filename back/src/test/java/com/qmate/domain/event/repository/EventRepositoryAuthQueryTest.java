package com.qmate.domain.event.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.qmate.config.JpaConfig;
import com.qmate.config.QuerydslConfig;
import com.qmate.domain.event.entity.Event;
import com.qmate.domain.event.entity.EventAlarmOption;
import com.qmate.domain.event.entity.EventRepeatType;
import com.qmate.domain.match.Match;
import com.qmate.domain.match.MatchMember;
import com.qmate.domain.user.User;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@DataJpaTest
@Import({JpaConfig.class, QuerydslConfig.class})
@Tag("local")
class EventRepositoryAuthQueryTest {

  @Autowired private TestEntityManager tem;
  @Autowired private EventRepository eventRepository;

  private Long matchId, userId, eventId;

  @BeforeEach
  void setUp() {

    // seed: Match + User(currentMatchId=matchId) + MatchMember + Event
    Match match = Match.builder()
        .startDate(LocalDateTime.now())
        .createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .build();
    tem.persist(match);

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

    Event event = Event.builder()
        .match(match)
        .title("제목")
        .description("설명")
        .eventAt(LocalDate.of(2025, 10, 9))
        .repeatType(EventRepeatType.NONE)
        .alarmOption(EventAlarmOption.WEEK_BEFORE)
        .anniversary(false)
        .createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .build();
    tem.persist(event);

    tem.flush();
    tem.clear();

    matchId = match.getId();
    userId = user.getId();
    eventId = event.getId();
  }

  @Test
  @DisplayName("findAuthorizedById - matchId/userId/eventId 모두 일치 시 Event 조회")
  void findAuthorizedById_success() {
    Optional<Event> found = eventRepository.findAuthorizedById(matchId, userId, eventId);
    assertThat(found).isPresent();
    assertThat(found.get().getId()).isEqualTo(eventId);
  }

  @Test
  @DisplayName("findAuthorizedById - currentMatch 불일치 시 empty")
  void findAuthorizedById_currentMismatch_empty() {
    // 사용자의 currentMatchId 변경 → 조인 조건 미충족
    tem.getEntityManager().createQuery("update User u set u.currentMatchId = :x where u.id = :id")
        .setParameter("x", 999L)
        .setParameter("id", userId)
        .executeUpdate();
    tem.clear();

    Optional<Event> found = eventRepository.findAuthorizedById(matchId, userId, eventId);
    assertThat(found).isEmpty();
  }

  @Test
  @DisplayName("findAuthorizedById - 다른 매치의 eventId로 조회 시 empty")
  void findAuthorizedById_wrongMatch_empty() {
    // 다른 매치에 이벤트를 하나 더 만들어 eventId만 바꿔 조회
    Match other = Match.builder()
        .startDate(LocalDateTime.now())
        .createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .build();
    tem.persist(other);

    Event otherEvent = Event.builder()
        .match(other)
        .title("다른매치")
        .eventAt(LocalDate.of(2025, 11, 1))
        .repeatType(EventRepeatType.NONE)
        .alarmOption(EventAlarmOption.WEEK_BEFORE)
        .anniversary(false)
        .createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .build();
    tem.persist(otherEvent);
    tem.flush();
    tem.clear();

    Optional<Event> found = eventRepository.findAuthorizedById(matchId, userId, otherEvent.getId());
    assertThat(found).isEmpty();
  }
}
