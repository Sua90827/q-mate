package com.qmate.domain.questioninstance.model.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class AnswerResponse {

  private Long answerId;
  private Long questionInstanceId;
  private String content;
  private LocalDateTime submittedAt;
  private LocalDateTime updatedAt;
}
