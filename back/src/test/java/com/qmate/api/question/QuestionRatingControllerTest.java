package com.qmate.api.question;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qmate.AuthTestUtils;
import com.qmate.SecuritySliceTestConfig;
import com.qmate.domain.questionrating.model.request.QuestionRatingRequest;
import com.qmate.domain.questionrating.model.response.CategoryLikeStat;
import com.qmate.domain.questionrating.model.response.CategoryLikeStatsResponse;
import com.qmate.domain.questionrating.model.response.QuestionRatingResponse;
import com.qmate.domain.questionrating.service.AdminRatingRebuildService;
import com.qmate.domain.questionrating.service.QuestionRatingService;
import com.qmate.domain.questionrating.service.QuestionRatingStatsService;
import com.qmate.exception.custom.question.DuplicateQuestionRatingException;
import com.qmate.exception.custom.question.QuestionNotFoundException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = QuestionRatingController.class)
@AutoConfigureMockMvc
@Import(SecuritySliceTestConfig.class)
class QuestionRatingControllerTest {

  @Autowired
  MockMvc mockMvc;

  @Autowired
  ObjectMapper objectMapper;

  @MockitoBean
  QuestionRatingService questionRatingService;
  @MockitoBean
  QuestionRatingStatsService questionRatingStatsService;
  @MockitoBean
  AdminRatingRebuildService questionRatingRebuildService;

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
            .with(AuthTestUtils.userPrincipal(99L))
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
            .with(AuthTestUtils.userPrincipal(99L))
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
        .andExpect(status().isConflict());
  }

  @Test
  @DisplayName("GET 전월 카테고리별 좋아요 통계: 200과 응답 DTO를 반환")
  void getPrevMonthLikesByCategory_success() throws Exception {
    // given
    Long userId = 99L;
    Long matchId = 123L;
    var anchorDate = LocalDate.of(2025, 9, 30); // → 전월: 2025-08

    var resp = CategoryLikeStatsResponse.builder()
        .matchId(matchId)
        .month(YearMonth.of(2025, 8))
        .from(LocalDateTime.of(2025, 8, 1, 0, 0, 0))
        .to(LocalDateTime.of(2025, 8, 31, 23, 59, 59))
        .totalLikes(22L)
        .categories(List.of(
            CategoryLikeStat.builder().categoryId(1L).categoryName("일상").likeCount(10L).build(),
            CategoryLikeStat.builder().categoryId(3L).categoryName("가치관").likeCount(12L).build()
        ))
        .build();

    given(questionRatingStatsService.getPrevMonthLikesByCategory(eq(userId), eq(matchId), eq(anchorDate)))
        .willReturn(resp);

    // when & then
    mockMvc.perform(get("/api/matches/{matchId}/stats/likes-by-category/monthly", matchId)
            .with(AuthTestUtils.userPrincipal(userId))
            .param("anchorDate", "2025-09-30"))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.matchId", is(matchId.intValue())))
        .andExpect(jsonPath("$.month", is("2025-08")))
        .andExpect(jsonPath("$.totalLikes", is(22)))
        .andExpect(jsonPath("$.categories[0].categoryName", is("일상")))
        .andExpect(jsonPath("$.categories[1].likeCount", is(12)));

    then(questionRatingStatsService).should()
        .getPrevMonthLikesByCategory(eq(userId), eq(matchId), eq(anchorDate));
  }

}
