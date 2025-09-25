package com.qmate.api.questioninstance;

import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.anyLong;
import static org.mockito.BDDMockito.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qmate.AuthTestUtils;
import com.qmate.SecuritySliceTestConfig;
import com.qmate.domain.questioninstance.model.request.AnswerContentRequest;
import com.qmate.domain.questioninstance.model.response.AnswerResponse;
import com.qmate.domain.questioninstance.service.AnswerService;
import com.qmate.exception.custom.questioninstance.AnswerAlreadyExistsException;
import com.qmate.exception.custom.questioninstance.AnswerCannotModifyException;
import com.qmate.exception.custom.questioninstance.QuestionInstanceForbiddenException;
import com.qmate.exception.custom.questioninstance.QuestionInstanceNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@WebMvcTest(controllers = AnswerController.class)
@AutoConfigureMockMvc
@Import(SecuritySliceTestConfig.class)
class AnswerControllerCreateTest {

  @Autowired
  MockMvc mockMvc;
  @Autowired
  ObjectMapper objectMapper;

  @MockitoBean
  AnswerService answerService;

  @Test
  @DisplayName("201 Created: Location은 QI 단건, 바디는 생성 결과")
  void create_201() throws Exception {
    // given
    Long qiId = 123L;
    var req = new AnswerContentRequest("최대 100자");
    // userId는 컨트롤러 내부 구현(현재는 1L, 이후 principal)과 무관하게 anyLong()로 대응
    var res = new AnswerResponse(
        456L, qiId, "최대 100자",
        LocalDateTime.parse("2025-09-11T12:20:00"),
        LocalDateTime.parse("2025-09-11T12:20:00")
    );

    given(answerService.create(eq(qiId), anyLong(), any(AnswerContentRequest.class)))
        .willReturn(res);

    // expect
    mockMvc.perform(post("/api/question-instances/{qiId}/answers", qiId)
            .with(AuthTestUtils.userPrincipal(1L))
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(req)))
        .andExpect(status().isCreated())
        .andExpect(header().string("Location", "/api/question-instances/" + qiId))
        .andExpect(jsonPath("$.answerId").value(456))
        .andExpect(jsonPath("$.questionInstanceId").value(123))
        .andExpect(jsonPath("$.content").value("최대 100자"))
        .andExpect(jsonPath("$.submittedAt").value("2025-09-11T12:20:00"));
  }

  @Test
  @DisplayName("400 Bad Request: 본문 검증 실패(@NotBlank)")
  void create_400_validation() throws Exception {
    // given
    var bad = new AnswerContentRequest(""); // @NotBlank 위반

    // expect
    mockMvc.perform(post("/api/question-instances/{qiId}/answers", 1L)
            .with(AuthTestUtils.userPrincipal(1L))
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(bad)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("403 Forbidden: 매치 불일치")
  void create_403_forbidden() throws Exception {
    // given
    Long qiId = 10L;
    var req = new AnswerContentRequest("ok");
    willThrow(new QuestionInstanceForbiddenException())
        .given(answerService).create(eq(qiId), anyLong(), any(AnswerContentRequest.class));

    // expect
    mockMvc.perform(post("/api/question-instances/{qiId}/answers", qiId)
            .with(AuthTestUtils.userPrincipal(1L))
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(req)))
        .andExpect(status().isForbidden());
  }

  @Test
  @DisplayName("423 Locked: QI 상태가 완료/만료라 수정 불가")
  void create_423_locked() throws Exception {
    // given
    Long qiId = 11L;
    var req = new AnswerContentRequest("ok");
    willThrow(new AnswerCannotModifyException())
        .given(answerService).create(eq(qiId), anyLong(), any(AnswerContentRequest.class));

    // expect
    mockMvc.perform(post("/api/question-instances/{qiId}/answers", qiId)
            .with(AuthTestUtils.userPrincipal(1L))
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(req)))
        .andExpect(status().isLocked());
  }

  @Test
  @DisplayName("409 Conflict: 동일 QI에 본인 답변 기등록")
  void create_409_conflict_duplicate() throws Exception {
    // given
    Long qiId = 12L;
    var req = new AnswerContentRequest("ok");
    willThrow(new AnswerAlreadyExistsException())
        .given(answerService).create(eq(qiId), anyLong(), any(AnswerContentRequest.class));

    // expect
    mockMvc.perform(post("/api/question-instances/{qiId}/answers", qiId)
            .with(AuthTestUtils.userPrincipal(1L))
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(req)))
        .andExpect(status().isConflict());
  }

  @Test
  @DisplayName("404 Not Found: QI가 없음")
  void create_404_qiNotFound() throws Exception {
    // given
    Long qiId = 404L;
    var req = new AnswerContentRequest("ok");
    willThrow(new QuestionInstanceNotFoundException())
        .given(answerService).create(eq(qiId), anyLong(), any(AnswerContentRequest.class));

    // expect
    mockMvc.perform(post("/api/question-instances/{qiId}/answers", qiId)
            .with(AuthTestUtils.userPrincipal(1L))
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(req)))
        .andExpect(status().isNotFound());
  }
}
