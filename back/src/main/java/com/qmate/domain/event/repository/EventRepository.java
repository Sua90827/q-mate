package com.qmate.domain.event.repository;

import com.qmate.domain.event.entity.Event;
import com.qmate.domain.event.entity.EventRepeatType;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
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


  // 생일 이벤트 조회 (user가 속한 매치의 anniversary YEARLY 이벤트 중 oldMonth/oldDay에 해당하는 것)
  @Query("""
        select e from Event e
        join e.match m
        join MatchMember mm on mm.match = m and mm.user.id = :userId
        where e.anniversary = true
          and e.repeatType = :repeatType
          and function('MONTH', e.eventAt) = :month
          and function('DAY', e.eventAt) = :day
      """)
  List<Event> findBirthdayEventsForUserByMonthDay(
      @Param("userId") Long userId,
      @Param("repeatType") EventRepeatType repeatType,
      @Param("month") int month,
      @Param("day") int day
  );

  // 매치의 100일 이벤트 조회 (반복 없음)
  @Query("""
      select e from Event e
      where e.match.id = :matchId
        and e.anniversary = true
        and e.repeatType = 'NONE'
    """)
  Optional<Event> findHundredDayEventByMatchId(@Param("matchId") Long matchId);

  // 매치의 주년 이벤트 조회 (반복 YEARLY)
  @Query("""
      select e from Event e
      where e.match.id = :matchId
        and e.anniversary = true
        and e.repeatType = 'YEARLY'
    """)
  Optional<Event> findAnniversaryEventByMatchId(@Param("matchId") Long matchId);



  @Query("""
      select e
      from Event e
      where e.alarmOption <> com.qmate.domain.event.entity.EventAlarmOption.NONE
        and e.eventAt <= :to
      """)
  List<Event> findAlarmCandidates(@Param("to") LocalDate to);

}