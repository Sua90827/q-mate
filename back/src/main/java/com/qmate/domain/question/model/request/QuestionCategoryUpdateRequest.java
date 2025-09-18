package com.qmate.domain.question.model.request;

import com.qmate.common.constants.question.QuestionCategoryConstants;
import com.qmate.domain.question.entity.RelationType;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuestionCategoryUpdateRequest {

  @Size(max = QuestionCategoryConstants.MAX_NAME_LENGTH, message = QuestionCategoryConstants.NAME_SIZE_MESSAGE)
  private String name;

  private RelationType relationType;

  private Boolean isActive;
}
