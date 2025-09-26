package com.qmate.domain.questionrating.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionRatingResponse {
  private Long ratingId;
  private Long questionId;
  private Long userId;

  @JsonProperty("isLike")
  private boolean isLike;

  private LocalDateTime createdAt;

}
