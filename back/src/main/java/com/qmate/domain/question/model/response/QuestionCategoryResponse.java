package com.qmate.domain.question.model.response;

import com.qmate.domain.question.entity.RelationType;
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
  private RelationType relationType;
  private boolean isActive;
}