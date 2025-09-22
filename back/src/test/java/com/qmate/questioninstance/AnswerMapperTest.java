package com.qmate.questioninstance;

import static org.assertj.core.api.Assertions.assertThat;

import com.qmate.domain.questioninstance.entity.Answer;
import com.qmate.domain.questioninstance.entity.QuestionInstance;
import com.qmate.domain.questioninstance.mapper.AnswerMapper;
import com.qmate.domain.questioninstance.model.request.AnswerContentRequest;
import com.qmate.domain.questioninstance.model.response.AnswerCreateResponse;
import com.qmate.domain.user.User;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class AnswerMapperTest {

  @Test
  void toEntity_트리밍_개행정규화() {
    QuestionInstance qi = QuestionInstance.builder().id(123L).build();
    User user = User.builder().id(99L).build();

    AnswerContentRequest req = new AnswerContentRequest("  안녕\r\n하세요  ");
    Answer a = AnswerMapper.toEntity(qi, user, req);

    assertThat(a.getQuestionInstance()).isSameAs(qi);
    assertThat(a.getUser()).isSameAs(user);
    assertThat(a.getContent()).isEqualTo("안녕\n하세요");
  }

  @Test
  void toResponse_엔티티에서필드매핑() {
    QuestionInstance qi = QuestionInstance.builder().id(123L).build();
    User user = User.builder().id(99L).build();
    Answer a = Answer.builder()
        .id(456L)
        .questionInstance(qi)
        .user(user)
        .content("hello")
        .submittedAt(LocalDateTime.parse("2025-09-11T12:20:00"))
        .build();

    AnswerCreateResponse res = AnswerMapper.toResponse(a);

    assertThat(res.getAnswerId()).isEqualTo(456L);
    assertThat(res.getQuestionInstanceId()).isEqualTo(123L);
    assertThat(res.getUserId()).isEqualTo(99L);
    assertThat(res.getContent()).isEqualTo("hello");
    assertThat(res.getSubmittedAt()).isEqualTo("2025-09-11T12:20:00");
  }
}
