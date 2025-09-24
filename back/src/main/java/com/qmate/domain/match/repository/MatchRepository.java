package com.qmate.domain.match.repository;

import com.qmate.domain.match.Match;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MatchRepository extends JpaRepository<Match, Long> {


  @EntityGraph(attributePaths = {"members", "members.user"})
  Optional<Match> findWithMembersAndUsersById(Long id);
}
