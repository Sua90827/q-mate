package com.qmate.domain.match.repository;

import com.qmate.domain.match.Match;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MatchRepository extends JpaRepository<Match, Long> {

  /**
   * ID로 Match를 조회할 때, 연관된 members와 각 member의 user 정보까지
   * 한 번의 쿼리로 함께 조회(FETCH)하여 N+1 문제를 해결합니다.
   * @param id 조회할 매칭의 ID
   * @return Match 엔티티 (members와 users 정보가 채워진 상태)
   */
  @Override
  @EntityGraph(attributePaths = {"members", "members.user"})
  Optional<Match> findById(Long id);
}
