package com.qmate.domain.question.repository;

import com.qmate.domain.question.entity.CustomQuestion;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CustomQuestionRepository extends JpaRepository<CustomQuestion, Long>, CustomQuestionQueryRepository {

  @EntityGraph(attributePaths = "match")
  Optional<CustomQuestion> findByIdAndCreatedBy(Long id, Long userId);

  @Query("""
    select cq
    from CustomQuestion cq
    where cq.match.id = :matchId
      and not exists (
        select 1
        from QuestionInstance qi
        where qi.match.id = :matchId
          and qi.customQuestion.id = cq.id
      )
    order by cq.id asc
    """)
  List<CustomQuestion> findFirstUnusedForMatchOrderByIdAsc(@Param("matchId") Long matchId, Pageable pageable);
}