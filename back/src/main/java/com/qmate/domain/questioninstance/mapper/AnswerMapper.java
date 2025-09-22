package com.qmate.domain.questioninstance.mapper;

import com.qmate.domain.questioninstance.entity.Answer;
import com.qmate.domain.questioninstance.entity.QuestionInstance;
import com.qmate.domain.questioninstance.model.request.AnswerContentRequest;
import com.qmate.domain.questioninstance.model.response.AnswerCreateResponse;
import com.qmate.domain.user.User;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class AnswerMapper {

  public static Answer toEntity(QuestionInstance qi, User user, AnswerContentRequest req) {
    String normalized = normalize(req.getContent());
    return Answer.builder()
        .questionInstance(qi)
        .user(user)
        .content(normalized)
        .build();
  }

  public static AnswerCreateResponse toResponse(Answer a) {
    return AnswerCreateResponse.builder()
        .answerId(a.getId())
        .questionInstanceId(a.getQuestionInstance().getId())
        .userId(a.getUser().getId())
        .content(a.getContent())
        .submittedAt(a.getSubmittedAt())
        .build();
  }

  private static String normalize(String raw) {
    if (raw == null) return null;
    return raw.trim().replace("\r\n", "\n");
  }
}
