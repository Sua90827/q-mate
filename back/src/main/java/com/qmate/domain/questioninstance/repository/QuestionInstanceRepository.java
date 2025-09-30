package com.qmate.domain.questioninstance.repository;

import com.qmate.domain.questioninstance.entity.QuestionInstance;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
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
  @QueryHints(@QueryHint(name = "jakarta.persistence.lock.timeout", value = "3000")) // ms
  Optional<QuestionInstance> findByIdForUpdate(Long qiId);

  boolean existsByCustomQuestion_Id(Long customQuestionId);
}