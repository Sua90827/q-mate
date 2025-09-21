package com.qmate.domain.questioninstance.repository;

import com.qmate.domain.questioninstance.entity.Answer;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswerRepository extends JpaRepository<Answer, Long> {

  Optional<Answer> findByQuestionInstance_IdAndUser_Id(Long questionInstanceId, Long userId);
}