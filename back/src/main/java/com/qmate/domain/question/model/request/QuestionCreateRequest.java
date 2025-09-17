package com.qmate.domain.question.model.request;

import com.qmate.common.constants.question.QuestionConstants;
import com.qmate.domain.question.entity.RelationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuestionCreateRequest {

  @NotNull(message = QuestionConstants.CATEGORY_ID_NOT_NULL_MESSAGE)
  private Long categoryId;

  @NotNull(message = QuestionConstants.RELATION_TYPE_NOT_BLANK_MESSAGE)
  private RelationType relationType;

  @NotBlank(message = QuestionConstants.TEXT_NOT_BLANK_MESSAGE)
  @Size(max = QuestionConstants.MAX_TEXT_LENGTH, message = QuestionConstants.TEXT_SIZE_MESSAGE)
  private String text;
}
