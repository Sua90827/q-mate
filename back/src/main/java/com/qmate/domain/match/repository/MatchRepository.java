package com.qmate.domain.match.repository;

import com.qmate.domain.match.Match;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

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
}
