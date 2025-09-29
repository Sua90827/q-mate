package com.qmate.api.question;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.qmate.AuthTestUtils;
import com.qmate.SecuritySliceTestConfig;
import com.qmate.domain.match.RelationType;
import com.qmate.domain.question.model.request.CustomQuestionStatusFilter;
import com.qmate.domain.question.model.response.CustomQuestionResponse;
import com.qmate.domain.question.model.response.SourceType;
import com.qmate.domain.question.service.CustomQuestionService;
import com.qmate.exception.custom.question.CustomQuestionInvalidSortKeyException;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = CustomQuestionController.class)
@AutoConfigureMockMvc
@Import(SecuritySliceTestConfig.class)
class CustomQuestionControllerTest {

  @Autowired
  MockMvc mvc;

  @MockitoBean
  CustomQuestionService customQuestionService;

  private CustomQuestionResponse dto(long id, long matchId, boolean editable, LocalDateTime cAt, LocalDateTime uAt) {
    return new CustomQuestionResponse(
        id,
        SourceType.CUSTOM,
        RelationType.COUPLE, // 아무 관계타입이나 고정
        matchId,
        "질문-" + id,
        editable,
        cAt,
        uAt
    );
  }

  @Nested
  @DisplayName("성공")
  class Success {

    @Test
    @DisplayName("기본 파라미터로 목록 조회 200")
    void list_ok_defaultParams() throws Exception {
      // given
      long userId = 1L;
      long matchId = 10L;
      var cAt = LocalDateTime.parse("2025-09-11T12:00:00");
      var uAt = LocalDateTime.parse("2025-09-11T12:34:56");

      Page<CustomQuestionResponse> page = new PageImpl<>(
          List.of(dto(101, matchId, true, cAt, uAt), dto(102, matchId, false, cAt, uAt)),
          PageRequest.of(0, 20, Sort.by(Sort.Order.desc("createdAt"))),
          2
      );

      given(customQuestionService.findPageByOwnerAndStatusFilter(eq(userId), eq(matchId), isNull(), any(Pageable.class)))
          .willReturn(page);

      // when & then
      mvc.perform(get("/api/matches/{matchId}/custom-questions", matchId)
              .with(AuthTestUtils.userPrincipal(userId))
              .accept(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.content", hasSize(2)))
          .andExpect(jsonPath("$.content[0].customQuestionId").value(101))
          .andExpect(jsonPath("$.content[0].sourceType").value("CUSTOM"))
          .andExpect(jsonPath("$.content[0].relationType").value("COUPLE"))
          .andExpect(jsonPath("$.content[0].matchId").value((int) matchId))
          .andExpect(jsonPath("$.content[0].text").value("질문-101"))
          .andExpect(jsonPath("$.content[0].isEditable").value(true))
          .andExpect(jsonPath("$.content[0].createdAt").value("2025-09-11T12:00:00"))
          .andExpect(jsonPath("$.content[0].updatedAt").value("2025-09-11T12:34:56"))
          .andExpect(jsonPath("$.totalElements").value(2));

      then(customQuestionService).should()
          .findPageByOwnerAndStatusFilter(eq(userId), eq(matchId), isNull(), any(Pageable.class));
    }

    @Test
    @DisplayName("status/정렬 파라미터 포함 목록 조회 200")
    void list_ok_withStatusAndSort() throws Exception {
      long userId = 1L;
      long matchId = 10L;
      Page<CustomQuestionResponse> empty = Page.empty();

      given(customQuestionService.findPageByOwnerAndStatusFilter(eq(userId), eq(matchId),
          eq(CustomQuestionStatusFilter.COMPLETED), any(Pageable.class)))
          .willReturn(empty);

      mvc.perform(get("/api/matches/{matchId}/custom-questions", matchId)
              .with(AuthTestUtils.userPrincipal(userId))
              .param("status", "COMPLETED")
              .param("sort", "createdAt,desc")
              .param("sort", "updatedAt,asc")
              .param("page", "0")
              .param("size", "20")
              .accept(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.content", hasSize(0)));

      then(customQuestionService).should()
          .findPageByOwnerAndStatusFilter(eq(userId), eq(matchId),
              eq(CustomQuestionStatusFilter.COMPLETED), any(Pageable.class));
    }
  }

  @Nested
  @DisplayName("실패")
  class Failure {

    @Test
    @DisplayName("status 소문자 등 enum 바인딩 실패 → 400")
    void list_invalidStatus_badRequest() throws Exception {
      mvc.perform(get("/api/matches/{matchId}/custom-questions", 10L)
              .with(AuthTestUtils.userPrincipal(1L))
              .param("status", "editable")) // 소문자 → 매핑 실패
          .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("지원하지 않는 정렬 키 → 400")
    void list_invalidSortKey_badRequest() throws Exception {
      long userId = 1L;
      long matchId = 10L;

      given(customQuestionService.findPageByOwnerAndStatusFilter(eq(userId), eq(matchId),
          isNull(), any(Pageable.class)))
          .willThrow(new CustomQuestionInvalidSortKeyException());

      mvc.perform(get("/api/matches/{matchId}/custom-questions", matchId)
              .with(AuthTestUtils.userPrincipal(userId))
              .param("sort", "unknown,asc")
              .accept(MediaType.APPLICATION_JSON))
          .andExpect(status().isBadRequest());
    }
  }
}
