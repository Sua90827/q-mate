package com.qmate.domain.question.model.request;

import com.qmate.common.constants.question.QuestionConstants;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomQuestionTextRequest {

  @NotBlank(message = QuestionConstants.TEXT_NOT_BLANK_MESSAGE)
  @Size(max = QuestionConstants.MAX_TEXT_LENGTH, message = QuestionConstants.TEXT_SIZE_MESSAGE)
  private String text;
}
