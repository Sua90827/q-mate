package com.qmate.domain.questionrating.mapper;

import com.qmate.domain.question.entity.Question;
import com.qmate.domain.questionrating.entity.QuestionRating;
import com.qmate.domain.questionrating.model.request.QuestionRatingRequest;
import com.qmate.domain.questionrating.model.response.QuestionRatingResponse;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class QuestionRatingMapper {

  public static QuestionRating toEntity(QuestionRatingRequest request, Question question) {
    return QuestionRating.builder()
        .question(question)
        .isLike(request.getIsLike())
        .build();
  }

  public static QuestionRatingResponse toResponse(QuestionRating questionRating) {
    return QuestionRatingResponse.builder()
        .ratingId(questionRating.getId())
        .questionId(questionRating.getQuestion().getId())
        .userId(questionRating.getUserId())
        .isLike(questionRating.isLike())
        .createdAt(questionRating.getCreatedAt())
        .build();
  }
}
