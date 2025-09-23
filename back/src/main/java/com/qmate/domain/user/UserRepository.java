package com.qmate.domain.user;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

  boolean existsByEmail(String email);


  Optional<User> findByEmail(String email);

  // currentMatchId가 matchId이고, 내가 아닌 사용자
  Optional<User> findByCurrentMatchIdAndIdNot(Long matchId, Long excludeUserId);

}
