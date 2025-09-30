package com.qmate.domain.question.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.qmate.domain.match.RelationType;
import java.time.LocalDateTime;
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
  private SourceType sourceType;     // "ADMIN" | "CUSTOM"
  private RelationType relationType;   // "COUPLE" | "FRIEND"
  private Long matchId;

  private String text;

  @JsonProperty("isEditable") // api 명세 맞춤
  private boolean editable;

  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}