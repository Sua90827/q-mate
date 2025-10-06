package com.qmate.domain.match.repository;

import com.qmate.domain.match.Match;
import com.qmate.domain.match.MatchStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MatchRepository extends JpaRepository<Match, Long>, MatchRepositoryCustom {


  @EntityGraph(attributePaths = {"members", "members.user"})
  Optional<Match> findWithMembersAndUsersById(Long id);

  @Query("""
    select m
    from Match m
      join MatchMember mm on mm.match = m and mm.user.id = :userId
      join User u on u.id = :userId
    where m.id = :matchId
      and u.currentMatchId = :matchId
  """)
  Optional<Match> findAuthorizedById(Long matchId, Long userId);

  /**
   * ACTIVE 매치 중, match_setting.daily_question_hour == :hour 인 매치 목록 조회.
   */
  @Query("""
      select m
      from Match m
        join MatchSetting ms on ms.match.id = m.id
      where m.status = :status
        and ms.dailyQuestionHour = :hour
      """)
  List<Match> findAllActiveByDailyHour(@Param("hour") int hour,
      @Param("status") MatchStatus status);
}
