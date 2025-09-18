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
public class CustomQuestionResponse {

  private Long customQuestionId;
  private String sourceType;     // "ADMIN" | "CUSTOM"
  private String relationType;   // "COUPLE" | "FRIEND" | "BOTH"
  private Long matchId;

  private String text;

  @JsonProperty("isEditable") // api 명세 맞춤
  private boolean editable;

  private String createdAt;
  private String updatedAt;
}