package com.qmate.domain.match.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.qmate.config.JpaConfig;
import com.qmate.domain.event.entity.Event;
import com.qmate.domain.event.entity.EventAlarmOption;
import com.qmate.domain.event.entity.EventRepeatType;
import com.qmate.domain.match.Match;
import com.qmate.domain.match.MatchMember;
import com.qmate.domain.match.MatchStatus;
import com.qmate.domain.match.RelationType;
import com.qmate.domain.user.User;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
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
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@DataJpaTest
@Import(JpaConfig.class)
@Tag("local")
class MatchRepositoryAuthQueryTest {

  @Autowired private TestEntityManager tem;
  @Autowired private MatchRepository matchRepository;

  private Long matchId;
  private Long userId;

  @TestConfiguration
  static class QuerydslTestConfig {

    @Bean
    JPAQueryFactory jpaQueryFactory(EntityManager em) {
      return new JPAQueryFactory(em);
    }
  }

  @BeforeEach
  void setUp() {

    // given: User( currentMatchId = matchId ) + Match + MatchMember
    Match match = Match.builder()
        .relationType(RelationType.COUPLE)
        .status(MatchStatus.ACTIVE)
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

    // event는 이 테스트에선 필수는 아니지만, 스키마/연관 무결성 확인용으로 하나 심어둠
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
  }

  @Test
  @DisplayName("findAuthorizedById - 매치 멤버 & currentMatch 일치 시 조회 성공")
  void findAuthorizedById_success() {
    Optional<Match> found = matchRepository.findAuthorizedById(matchId, userId);
    assertThat(found).isPresent();
    assertThat(found.get().getId()).isEqualTo(matchId);
  }

  @Test
  @DisplayName("findAuthorizedById - currentMatch 불일치 시 empty")
  void findAuthorizedById_currentMismatch_empty() {
    // when: 사용자의 currentMatchId를 다른 값으로 바꾸면
    User user = tem.find(User.class, userId);
    user.setCurrentMatchId(999L);
    tem.flush();
    tem.clear();

    Optional<Match> found = matchRepository.findAuthorizedById(matchId, userId);
    assertThat(found).isEmpty();
  }
}
