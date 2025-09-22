package com.qmate.domain.questioninstance.model.request;

import com.qmate.common.constants.questioninstance.AnswerConstants;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AnswerContentRequest {

  @NotBlank(message = AnswerConstants.CONTENT_NOT_BLANK_MESSAGE)
  @Size(max = AnswerConstants.MAX_CONTENT_LENGTH, message = AnswerConstants.CONTENT_SIZE_MESSAGE)
  private String content;
}
