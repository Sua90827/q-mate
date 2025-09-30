package com.qmate.api.questioninstance;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qmate.AuthTestUtils;
import com.qmate.SecuritySliceTestConfig;
import com.qmate.domain.questioninstance.model.request.AnswerContentRequest;
import com.qmate.domain.questioninstance.model.response.AnswerResponse;
import com.qmate.domain.questioninstance.service.AnswerService;
import com.qmate.exception.custom.questioninstance.AnswerCannotModifyException;
import com.qmate.exception.custom.questioninstance.AnswerNotFoundException;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = AnswerController.class)
@AutoConfigureMockMvc
@Import(SecuritySliceTestConfig.class)
class AnswerControllerUpdateTest {

  @Autowired
  MockMvc mockMvc;
  @Autowired
  ObjectMapper objectMapper;

  @MockitoBean
  AnswerService answerService;

  @Test
  @DisplayName("200 OK: 수정 성공 시 공통 응답 반환")
  void update_200_ok() throws Exception {
    Long answerId = 1L;
    var req = new AnswerContentRequest("수정된 내용");
    var res = new AnswerResponse(
        answerId, 123L, "수정된 내용",
        LocalDateTime.parse("2025-09-11T12:00:00"),
        LocalDateTime.parse("2025-09-11T12:30:00")
    );

    given(answerService.update(eq(answerId), anyLong(), any(AnswerContentRequest.class)))
        .willReturn(res);

    mockMvc.perform(patch("/api/answers/{answerId}", answerId)
            .with(AuthTestUtils.userPrincipal(1L))
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(req)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.answerId").value(answerId))
        .andExpect(jsonPath("$.questionInstanceId").value(123))
        .andExpect(jsonPath("$.content").value("수정된 내용"))
        .andExpect(jsonPath("$.submittedAt").value("2025-09-11T12:00:00"))
        .andExpect(jsonPath("$.updatedAt").value("2025-09-11T12:30:00"));

    then(answerService).should(times(1))
        .update(eq(answerId), anyLong(), any(AnswerContentRequest.class));
  }

  @Test
  @DisplayName("content > 100 → 400 Bad Request (컨트롤러 @Valid)")
  void update_content_too_long_400() throws Exception {
    String tooLong = "a".repeat(101);
    String body = objectMapper.writeValueAsString(new AnswerContentRequest(tooLong));

    mockMvc.perform(
            patch("/api/answers/{answerId}", 1L)
                .with(AuthTestUtils.userPrincipal(1L))
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
        .andExpect(status().isBadRequest());

    Mockito.verifyNoInteractions(answerService);
  }

  @Test
  @DisplayName("423 Locked: QI 상태가 완료/만료라 수정 불가")
  void update_423_locked() throws Exception {
    // given
    Long answerId = 11L;
    var req = new AnswerContentRequest("ok");
    willThrow(new AnswerCannotModifyException())
        .given(answerService).update(eq(answerId), anyLong(), any(AnswerContentRequest.class));

    // expect
    mockMvc.perform(patch("/api/answers/{answerId}", answerId)
            .with(AuthTestUtils.userPrincipal(1L))
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(req)))
        .andExpect(status().isLocked());
  }

  @Test
  @DisplayName("404 Not Found: answer가 없음")
  void update_404_answerNotFound() throws Exception {
    // given
    Long answerId = 404L;
    var req = new AnswerContentRequest("ok");
    willThrow(new AnswerNotFoundException())
        .given(answerService).update(eq(answerId), anyLong(), any(AnswerContentRequest.class));

    // expect
    mockMvc.perform(patch("/api/answers/{answerId}", answerId)
            .with(AuthTestUtils.userPrincipal(1L))
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(req)))
        .andExpect(status().isNotFound());
  }

}
