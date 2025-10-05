package com.qmate.domain.event.repository;

import com.qmate.domain.event.entity.Event;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EventRepository extends JpaRepository<Event, Long>, EventQueryRepository {

  @Query("""
      select e
      from Event e
        join e.match m
        join MatchMember mm on mm.match = m and mm.user.id = :userId
        join User u on u.id = :userId
      where e.id = :eventId
        and m.id = :matchId
        and u.currentMatchId = :matchId
      """)
  Optional<Event> findAuthorizedById(@Param("matchId") Long matchId,
      @Param("userId") Long userId,
      @Param("eventId") Long eventId);
}