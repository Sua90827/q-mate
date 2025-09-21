package com.qmate.domain.questioninstance.repository;

import com.qmate.domain.questioninstance.entity.QuestionInstance;
import java.util.Optional;

public interface QuestionInstanceQueryRepository {
  Optional<QuestionInstance> findDetailWithQuestionAndMatch(Long qiId);
}
