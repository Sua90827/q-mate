package com.qmate.domain.questioninstance.repository;

import com.qmate.domain.match.MatchStatus;
import com.qmate.domain.questioninstance.entity.QuestionInstance;
import com.qmate.domain.questioninstance.entity.QuestionInstanceStatus;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

public interface QuestionInstanceRepository extends JpaRepository<QuestionInstance, Long>, QuestionInstanceQueryRepository {

  @Query("""
      select distinct qi
      from QuestionInstance qi
        join fetch qi.match m
        join fetch m.members mm
        join fetch mm.user u
        left join fetch qi.question q
        left join fetch q.category qc
        left join fetch qi.customQuestion cq
      where qi.id = :qiId
        and m.id = (
          select usr.currentMatchId
          from User usr
          where usr.id = :requesterId
        )
      """)
  Optional<QuestionInstance> findDetailWithMatchMembersAndQuestionByIdIfRequesterInMatch(
      @Param("qiId") Long qiId,
      @Param("requesterId") Long requesterId
  );

  @Query("""
      select qi
      from QuestionInstance qi
      join fetch qi.match m
      join fetch m.members mm
      join fetch mm.user u
      where qi.id = :qiId
        and qi.match.id = (
          select u.currentMatchId
          from User u
          where u.id = :userId
        )
      """)
  Optional<QuestionInstance> findAuthorizedByIdForUser(
      @Param("qiId") Long questionInstanceId,
      @Param("userId") Long userId
  );

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("select qi from QuestionInstance qi where qi.id = :qiId")
  @QueryHints(@QueryHint(name = "jakarta.persistence.lock.timeout", value = "1000"))
    // ms
  Optional<QuestionInstance> findByIdForUpdate(Long qiId);

  boolean existsByCustomQuestion_Id(Long customQuestionId);

  List<QuestionInstance> findByMatchIdAndStatus(Long matchId, QuestionInstanceStatus status);

  @Query("""
      select distinct qi
      from QuestionInstance qi
        join fetch qi.match m
        join MatchSetting ms on ms.match.id = m.id
        join fetch m.members mm
        join fetch mm.user u
      where qi.status = :pending
        and qi.deliveredAt is null
        and m.status = :active
        and ms.dailyQuestionHour = :hour
      """)
  List<QuestionInstance> findPendingToDeliverForHourWithMembersAndUser(
      @Param("hour") int hour,
      @Param("active") MatchStatus active,
      @Param("pending") QuestionInstanceStatus pending
  );

  /**
   * 6시간 전의 "같은 시(hour)" 안에서 0~50분 사이에 delivered된, 아직 PENDING 상태인 QI 를 대상으로
   * - 매치가 ACTIVE 이고
   * - 해당 QI 에 대해 아직 답변(Answer)이 없는 사용자만 을 반환한다. (중복/불필요 조인 최소화)
   */
  @Query(value = """
      SELECT
          qi.question_instance_id AS qiId,
          m.match_id              AS matchId,
          mm.user_id              AS userId,
          u.push_enabled          AS pushEnabled
      FROM question_instance qi
      JOIN `match` m 
        ON m.match_id = qi.match_id 
       AND m.status   = 'ACTIVE'
      JOIN match_member mm 
        ON mm.match_id = m.match_id
      LEFT JOIN answer a 
        ON a.question_instance_id = qi.question_instance_id
       AND a.user_id              = mm.user_id
      JOIN `user` u 
        ON u.user_id = mm.user_id
      WHERE qi.status = 'PENDING'
        AND qi.delivered_at >= :start
        AND qi.delivered_at  < :end
        AND a.answer_id IS NULL
      """, nativeQuery = true)
  List<ReminderTargetRow> findReminderTargetsBetween(
      @Param("start") LocalDateTime start,
      @Param("end") LocalDateTime end
  );
}