package com.qmate.api.questioninstance;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.qmate.AuthTestUtils;
import com.qmate.SecuritySliceTestConfig;
import com.qmate.domain.questioninstance.entity.QuestionInstanceStatus;
import com.qmate.domain.questioninstance.model.response.QIListItem;
import com.qmate.domain.questioninstance.service.QuestionInstanceService;
import com.qmate.exception.custom.questioninstance.QIInvalidSortKeyException;
import com.qmate.exception.custom.questioninstance.QuestionInstanceForbiddenException;
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

@WebMvcTest(controllers = QuestionInstanceController.class)
@AutoConfigureMockMvc
@Import(SecuritySliceTestConfig.class)
class QIControllerListTest {

  @Autowired
  MockMvc mvc;
  @MockitoBean
  QuestionInstanceService questionInstanceService;

  @Nested
  @DisplayName("성공")
  class Success {

    @Test
    @DisplayName("기본 파라미터로 목록 조회 200")
    void list_ok_defaultParams() throws Exception {
      // given
      Long matchId = 10L;
      QIListItem item = new QIListItem(123L,
          LocalDateTime.parse("2025-09-11T12:00:00"),
          QuestionInstanceStatus.COMPLETED,
          "상대가 가장 좋아하는 음식은?",
          LocalDateTime.parse("2025-09-11T12:45:00"));

      Page<QIListItem> page = new PageImpl<>(
          List.of(item),
          PageRequest.of(0, 20, Sort.by(Sort.Order.desc("deliveredAt"))),
          1);

      given(questionInstanceService.list(
          anyLong(), eq(matchId), isNull(), isNull(), isNull(), any(Pageable.class)))
          .willReturn(page);

      // when & then
      mvc.perform(get("/api/matches/{matchId}/question-instances", matchId)
              .with(AuthTestUtils.userPrincipal(1L))
              .accept(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.content[0].questionInstanceId").value(123))
          .andExpect(jsonPath("$.content[0].status").value("COMPLETED"))
          .andExpect(jsonPath("$.content[0].text").value("상대가 가장 좋아하는 음식은?"))
          .andExpect(jsonPath("$.content[0].deliveredAt").value("2025-09-11T12:00:00"))
          .andExpect(jsonPath("$.content[0].completedAt").value("2025-09-11T12:45:00"))
          .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    @DisplayName("필터/정렬 파라미터 포함 목록 조회 200")
    void list_ok_withFiltersAndSort() throws Exception {
      Long matchId = 10L;
      Page<QIListItem> empty = Page.empty();

      given(questionInstanceService.list(
          anyLong(), eq(matchId), eq(QuestionInstanceStatus.COMPLETED),
          any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class)))
          .willReturn(empty);

      mvc.perform(get("/api/matches/{matchId}/question-instances", matchId)
              .param("status", "COMPLETED")
              .param("from", "2025-09-01T00:00:00")
              .param("to", "2025-09-30T23:59:59")
              .param("sort", "deliveredAt,desc")
              .param("sort", "status,asc")
              .param("page", "0")
              .param("size", "20")
              .with(AuthTestUtils.userPrincipal(1L))
              .accept(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.content", hasSize(0)));
    }
  }

  @Nested
  @DisplayName("실패")
  class Failure {

    @Test
    @DisplayName("권한 불일치 → 403")
    void list_forbidden() throws Exception {
      Long matchId = 999L;
      given(questionInstanceService.list(anyLong(), eq(matchId),
          any(), any(), any(), any()))
          .willThrow(new QuestionInstanceForbiddenException());

      mvc.perform(get("/api/matches/{matchId}/question-instances", matchId)
              .with(AuthTestUtils.userPrincipal(1L))
              .accept(MediaType.APPLICATION_JSON))
          .andExpect(status().isForbidden());
    }
  }

  @Test
  @DisplayName("지원하지 않는 정렬 키 → 400")
  void list_invalidSortKey_badRequest() throws Exception {
    Long matchId = 10L;

    given(questionInstanceService.list(
            anyLong(), eq(matchId), any(), any(), any(), any()))
        .willThrow(new QIInvalidSortKeyException());

    mvc.perform(get("/api/matches/{matchId}/question-instances", matchId)
            .with(AuthTestUtils.userPrincipal(1L))
            .param("sort", "unknownKey,asc")
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }
}
