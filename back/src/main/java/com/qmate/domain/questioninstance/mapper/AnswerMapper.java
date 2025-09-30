package com.qmate.domain.questioninstance.mapper;

import com.qmate.domain.questioninstance.entity.Answer;
import com.qmate.domain.questioninstance.entity.QuestionInstance;
import com.qmate.domain.questioninstance.model.request.AnswerContentRequest;
import com.qmate.domain.questioninstance.model.response.AnswerResponse;
import com.qmate.domain.user.User;
import java.time.LocalDateTime;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class AnswerMapper {

  public static Answer toEntity(QuestionInstance qi, AnswerContentRequest req) {
    String normalized = normalize(req.getContent());
    return Answer.builder()
        .questionInstance(qi)
        .content(normalized)
        .build();
  }

  public static AnswerResponse toResponse(Answer a) {
    return AnswerResponse.builder()
        .answerId(a.getId())
        .questionInstanceId(a.getQuestionInstance().getId())
        .content(a.getContent())
        .submittedAt(a.getSubmittedAt())
        .updatedAt(a.getUpdatedAt())
        .build();
  }

  public static String normalize(String raw) {
    if (raw == null) return null;
    return raw.trim().replace("\r\n", "\n");
  }
}
