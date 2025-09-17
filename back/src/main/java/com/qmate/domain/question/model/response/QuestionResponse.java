package com.qmate.domain.question.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuestionResponse {

  private Long questionId;
  private String sourceType;     // "ADMIN" | "CUSTOM"
  private String relationType;   // "COUPLE" | "FRIEND" | "BOTH"

  private CategoryInfo category;

  private String text;

  @JsonProperty("isActive") // api 명세 맞춤
  private boolean active;

  private String createdAt;
}