package com.qmate.questioninstance;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.qmate.api.questioninstance.QuestionInstanceController;
import com.qmate.domain.questioninstance.entity.InstanceStatus;
import com.qmate.domain.questioninstance.model.response.QIDetailResponse;
import com.qmate.domain.questioninstance.model.response.QIDetailResponse.AnswerView;
import com.qmate.domain.questioninstance.model.response.QIDetailResponse.CategoryInfo;
import com.qmate.domain.questioninstance.model.response.QIDetailResponse.QuestionInfo;
import com.qmate.domain.questioninstance.service.QuestionInstanceService;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = QuestionInstanceController.class)
@AutoConfigureMockMvc(addFilters = false)
class QuestionInstanceControllerTest {
  @Autowired
  MockMvc mockMvc;
  @MockitoBean
  QuestionInstanceService questionInstanceService;

  @Test
  @DisplayName("GET /api/question-instances/{id} — 모든 필드가 채워진 응답 반환")
  void getDetail_fullPayload() throws Exception {
    // given
    Long qiId = 123L;
    Long matchId = 10L;

    LocalDateTime deliveredAt = LocalDateTime.of(2025, 9, 11, 12, 0, 0);
    LocalDateTime completedAt = LocalDateTime.of(2025, 9, 11, 12, 45, 0);

    QIDetailResponse dto = QIDetailResponse.builder()
        .questionInstanceId(qiId)
        .matchId(matchId)
        .deliveredAt(deliveredAt)
        .status(InstanceStatus.COMPLETED)
        .completedAt(completedAt)
        .question(
            QuestionInfo.builder()
                .questionId(777L)
                .sourceType("ADMIN")
                .relationType("COUPLE")
                .category(CategoryInfo.builder().id(3L).name("취향").build())
                .text("연인이 가장 좋아하는 음식은?")
                .build()
        )
        .answers(List.of(
            AnswerView.builder()
                .answerId(456L)
                .userId(99L)
                .nickname("나")
                .isMine(true)
                .visible(true)
                .content("초밥!")
                .submittedAt(LocalDateTime.of(2025, 9, 11, 12, 20, 0))
                .build(),
            AnswerView.builder()
                .answerId(457L)
                .userId(100L)
                .nickname("너")
                .isMine(false)
                .visible(true)
                .content("파스타!")
                .submittedAt(LocalDateTime.of(2025, 9, 11, 12, 40, 0))
                .build()
        ))
        .build();

    // 컨트롤러가 requesterId=1L 로 서비스 호출한다고 가정
    when(questionInstanceService.getDetail(qiId, 1L)).thenReturn(dto);

    // expect
    mockMvc.perform(get("/api/question-instances/{id}", qiId))
        .andExpect(status().isOk())

        // top-level
        .andExpect(jsonPath("$.questionInstanceId").value(qiId))
        .andExpect(jsonPath("$.matchId").value(matchId))
        .andExpect(jsonPath("$.status").value("COMPLETED"))
        .andExpect(jsonPath("$.deliveredAt").value("2025-09-11T12:00:00"))
        .andExpect(jsonPath("$.completedAt").value("2025-09-11T12:45:00"))

        // question block
        .andExpect(jsonPath("$.question.questionId").value(777))
        .andExpect(jsonPath("$.question.sourceType").value("ADMIN"))
        .andExpect(jsonPath("$.question.relationType").value("COUPLE"))
        .andExpect(jsonPath("$.question.text").value("연인이 가장 좋아하는 음식은?"))
        .andExpect(jsonPath("$.question.category.id").value(3))
        .andExpect(jsonPath("$.question.category.name").value("취향"))

        // answers array size
        .andExpect(jsonPath("$.answers.length()").value(2))

        // answers[0] (mine)
        .andExpect(jsonPath("$.answers[0].answerId").value(456))
        .andExpect(jsonPath("$.answers[0].userId").value(99))
        .andExpect(jsonPath("$.answers[0].nickname").value("나"))
        .andExpect(jsonPath("$.answers[0].isMine").value(true))
        .andExpect(jsonPath("$.answers[0].visible").value(true))
        .andExpect(jsonPath("$.answers[0].content").value("초밥!"))
        .andExpect(jsonPath("$.answers[0].submittedAt").value("2025-09-11T12:20:00"))

        // answers[1] (partner)
        .andExpect(jsonPath("$.answers[1].answerId").value(457))
        .andExpect(jsonPath("$.answers[1].userId").value(100))
        .andExpect(jsonPath("$.answers[1].nickname").value("너"))
        .andExpect(jsonPath("$.answers[1].isMine").value(false))
        .andExpect(jsonPath("$.answers[1].visible").value(true))
        .andExpect(jsonPath("$.answers[1].content").value("파스타!"))
        .andExpect(jsonPath("$.answers[1].submittedAt").value("2025-09-11T12:40:00"));
  }
}
