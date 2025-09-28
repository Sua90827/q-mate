package com.qmate.domain.question.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.qmate.domain.question.entity.RelationType;
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
  private SourceType sourceType;     // "ADMIN" | "CUSTOM"
  private RelationType relationType;   // "COUPLE" | "FRIEND" | "BOTH"

  private CategoryInfo category;

  private String text;

  @JsonProperty("isActive") // api 명세 맞춤
  private boolean active;

  private String createdAt;
  private String updatedAt;
}