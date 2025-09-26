package com.qmate.domain.questionrating.repository.projection;

public interface RatingAgg {
  Long getQuestionId();
  Long getLikeCount();
  Long getDislikeCount();
}
