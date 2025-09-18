package com.qmate.domain.question.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuestionCategoryResponse {

  private Long id;
  private String name;
  private String relationType;
  private boolean isActive;
}