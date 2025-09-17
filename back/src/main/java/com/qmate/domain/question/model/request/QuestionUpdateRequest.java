package com.qmate.domain.question.model.request;

import com.qmate.common.constants.question.QuestionConstants;
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
public class QuestionUpdateRequest {

  private Long categoryId;

  private RelationType relationType;

  @Size(max = QuestionConstants.MAX_TEXT_LENGTH, message = QuestionConstants.TEXT_SIZE_MESSAGE)
  private String text;

  private Boolean active;
}
