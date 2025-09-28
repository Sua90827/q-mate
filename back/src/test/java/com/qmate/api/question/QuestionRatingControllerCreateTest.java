package com.qmate.api.question;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qmate.domain.questionrating.model.request.QuestionRatingRequest;
import com.qmate.domain.questionrating.model.response.QuestionRatingResponse;
import com.qmate.domain.questionrating.service.QuestionRatingService;
import com.qmate.exception.custom.question.DuplicateQuestionRatingException;
import com.qmate.exception.custom.question.QuestionNotFoundException;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = QuestionRatingController.class)
@AutoConfigureMockMvc(addFilters = false)
class QuestionRatingControllerCreateTest {

  @Autowired
  MockMvc mockMvc;
  @Autowired
  ObjectMapper objectMapper;

  @MockitoBean
  QuestionRatingService questionRatingService;

  @Test
  @DisplayName("create_success: 201과 응답 DTO를 반환")
  void create_success() throws Exception {
    Long qid = 777L;
    QuestionRatingResponse body = QuestionRatingResponse.builder()
        .ratingId(890L)
        .questionId(qid)
        .userId(99L)
        .isLike(true)
        .createdAt(LocalDateTime.of(2025, 9, 11, 13, 30))
        .build();

    given(questionRatingService.create(any(Long.class), any(QuestionRatingRequest.class)))
        .willReturn(body);

    String json = """
        { "isLike": true }
        """;

    mockMvc.perform(post("/api/questions/{questionId}/ratings", qid)
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
        .andExpect(status().isCreated())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.ratingId", is(890)))
        .andExpect(jsonPath("$.questionId", is(777)))
        .andExpect(jsonPath("$.userId", is(99)))
        .andExpect(jsonPath("$.isLike", is(true)))
        .andExpect(jsonPath("$.createdAt", is("2025-09-11T13:30:00")));
  }

  @Test
  @DisplayName("404 : 질문 없음이면 404")
  void create_fail_whenQuestionNotFound() throws Exception {
    Long qid = 777L;
    given(questionRatingService.create(any(Long.class), any(QuestionRatingRequest.class)))
        .willThrow(new QuestionNotFoundException());

    String json = """
        { "isLike": true }
        """;

    mockMvc.perform(post("/api/questions/{questionId}/ratings", qid)
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("409 : 중복이면 409")
  void create_fail_whenDuplicate() throws Exception {
    Long qid = 777L;
    given(questionRatingService.create(any(Long.class), any(QuestionRatingRequest.class)))
        .willThrow(new DuplicateQuestionRatingException());

    String json = """
        { "isLike": true }
        """;

    mockMvc.perform(post("/api/questions/{questionId}/ratings", qid)
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
        .andExpect(status().isConflict());
  }

}
