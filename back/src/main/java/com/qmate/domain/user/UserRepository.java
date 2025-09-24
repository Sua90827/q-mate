package com.qmate.domain.user;

import java.util.Optional;
import java.util.OptionalLong;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {

  boolean existsByEmail(String email);


  Optional<User> findByEmail(String email);

  // currentMatchId가 matchId이고, 내가 아닌 사용자
  Optional<User> findByCurrentMatchIdAndIdNot(Long matchId, Long excludeUserId);

  // userId로 현재 매치 아이디 조회
  @Query("select u.currentMatchId from User u where u.id = :userId")
  Optional<Long> findCurrentMatchIdById(@Param("userId") Long userId);

}
