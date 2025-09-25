package com.qmate.domain.questionrating.repository;

import com.qmate.domain.questionrating.entity.QuestionRating;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRatingRepository extends JpaRepository<QuestionRating, Long> {
  boolean existsByQuestion_IdAndUserId(Long questionId, Long userId);
}