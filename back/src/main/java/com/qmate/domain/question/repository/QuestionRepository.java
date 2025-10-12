package com.qmate.domain.question.repository;

import com.qmate.domain.question.entity.Question;
import com.qmate.domain.question.entity.RelationType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface QuestionRepository extends JpaRepository<Question, Long>, JpaSpecificationExecutor<Question> {

  @Override
  @EntityGraph(attributePaths = "category")
  Page<Question> findAll(Specification<Question> spec, Pageable pageable);

  Optional<Question> findFirstByCategory_NameAndRelationTypeAndIsActiveTrueOrderByIdAsc(
      String categoryName,
      RelationType relationType
  );

  @Query("""
    select q
    from Question q
      join q.category c
    where c.name = :categoryName
      and q.isActive = true
      and q.relationType = :couple
    order by q.id asc
    """)
  List<Question> findActiveCoupleByCategoryNameOrderByIdAsc(
      @Param("categoryName") String categoryName,
      @Param("couple") RelationType couple,
      Pageable pageable
  );

  @Query("""
      select q
      from Question q
        join Match m on m.id = :matchId
        join q.category c
      where q.isActive = true
        and c.name not like '기념일%'
        and (
             q.relationType = com.qmate.domain.question.entity.RelationType.BOTH
          or (m.relationType = com.qmate.domain.match.RelationType.COUPLE
              and q.relationType = com.qmate.domain.question.entity.RelationType.COUPLE)
          or (m.relationType = com.qmate.domain.match.RelationType.FRIEND
              and q.relationType = com.qmate.domain.question.entity.RelationType.FRIEND)
        )
        and not exists (
          select 1
          from QuestionInstance qi
          where qi.match.id = :matchId
            and qi.question.id = q.id
        )
      order by function('rand')
      """)
  List<Question> pickOneRandomUnusedAdminQuestion(@Param("matchId") Long matchId, Pageable pageable);
}