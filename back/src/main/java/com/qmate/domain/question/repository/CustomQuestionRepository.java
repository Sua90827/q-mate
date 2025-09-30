package com.qmate.domain.question.repository;

import com.qmate.domain.question.entity.CustomQuestion;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomQuestionRepository extends JpaRepository<CustomQuestion, Long>, CustomQuestionQueryRepository {

  @EntityGraph(attributePaths = "match")
  Optional<CustomQuestion> findByIdAndCreatedBy(Long id, Long userId);
}