package com.qmate.domain.questioninstance.repository;

import com.qmate.domain.questioninstance.entity.QuestionInstance;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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
}