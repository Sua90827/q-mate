package com.qmate.domain.questioninstance.repository;

import com.qmate.domain.questioninstance.entity.Answer;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswerRepository extends JpaRepository<Answer, Long> {

  Optional<Answer> findByQuestionInstance_IdAndUserId(Long questionInstanceId, Long userId);

  long countDistinctUserIdByQuestionInstance_Id(Long questionInstanceId);

  boolean existsByQuestionInstance_IdAndUserId(Long questionInstanceId, Long userId);

  @EntityGraph(attributePaths = "questionInstance")
  Optional<Answer> findByIdAndUserId(Long id, Long userId);
}