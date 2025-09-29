package com.qmate.domain.quetioninstance;

import static org.assertj.core.api.Assertions.assertThat;

import com.qmate.domain.questioninstance.entity.Answer;
import com.qmate.domain.questioninstance.entity.QuestionInstance;
import com.qmate.domain.questioninstance.mapper.AnswerMapper;
import com.qmate.domain.questioninstance.model.request.AnswerContentRequest;
import com.qmate.domain.questioninstance.model.response.AnswerResponse;
import com.qmate.domain.user.User;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class AnswerMapperTest {

  @Test
  void toEntity_트리밍_개행정규화() {
    QuestionInstance qi = QuestionInstance.builder().id(123L).build();
    User user = User.builder().id(99L).build();

    AnswerContentRequest req = new AnswerContentRequest("  안녕\r\n하세요  ");
    Answer a = AnswerMapper.toEntity(qi, req);

    assertThat(a.getQuestionInstance()).isSameAs(qi);
    assertThat(a.getUserId()).isSameAs(user.getId());
    assertThat(a.getContent()).isEqualTo("안녕\n하세요");
  }

  @Test
  void toResponse_엔티티에서필드매핑() {
    QuestionInstance qi = QuestionInstance.builder().id(123L).build();
    User user = User.builder().id(99L).build();
    Answer a = Answer.builder()
        .id(456L)
        .questionInstance(qi)
        .userId(user.getId())
        .content("hello")
        .submittedAt(LocalDateTime.parse("2025-09-11T12:20:00"))
        .build();

    AnswerResponse res = AnswerMapper.toResponse(a);

    assertThat(res.getAnswerId()).isEqualTo(456L);
    assertThat(res.getQuestionInstanceId()).isEqualTo(123L);
    assertThat(res.getContent()).isEqualTo("hello");
    assertThat(res.getSubmittedAt()).isEqualTo("2025-09-11T12:20:00");
  }
}
