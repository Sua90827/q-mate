package com.qmate.domain.questionrating.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class QuestionRatingRequest {

  @NotNull
  @JsonProperty("isLike")
  private Boolean isLike;
}
