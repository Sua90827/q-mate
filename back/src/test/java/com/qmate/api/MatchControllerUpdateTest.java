package com.qmate.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qmate.config.SecurityConfig;
import com.qmate.domain.auth.JwtService;
import com.qmate.domain.match.model.request.MatchUpdateRequest;
import com.qmate.domain.match.service.MatchService;
import com.qmate.exception.custom.matchinstance.MatchForbiddenException;
import com.qmate.exception.custom.matchinstance.MatchNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = MatchController.class)
@AutoConfigureMockMvc(addFilters = false) // Security 필터 비활성화
class MatchControllerUpdateTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private MatchService matchService;

  @Test
  @DisplayName("매칭 정보 업데이트 실패: 존재하지 않는 매칭 ID 요청 시 404 Not Found 응답")
  void updateMatchInfo_fail_404notFound() throws Exception {
    // given: 서비스가 MatchNotFoundException을 던지는 상황
    Long nonExistentMatchId = 404L;
    var request = new MatchUpdateRequest();

    willThrow(new MatchNotFoundException())
        .given(matchService).updateMatchInfo(anyLong(), anyLong(), any(MatchUpdateRequest.class));

    // expect: API를 호출하면, 404 Not Found 응답과 MATCH_002 에러 코드가 와야 함
    mockMvc.perform(patch("/api/matches/{matchId}/info", nonExistentMatchId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.errorCode").value("MATCH_002"));
  }

  @Test
  @DisplayName("매칭 정보 업데이트 실패: 권한 없는 접근 시 403 Forbidden 응답")
  void updateMatchInfo_fail_403forbidden() throws Exception {
    // given: 서비스가 MatchForbiddenException을 던지는 상황
    Long matchId = 100L;
    var request = new MatchUpdateRequest();

    willThrow(new MatchForbiddenException())
        .given(matchService).updateMatchInfo(anyLong(), anyLong(), any(MatchUpdateRequest.class));

    // expect: API를 호출하면, 403 Forbidden 응답과 MATCH_008 에러 코드가 와야 함
    mockMvc.perform(patch("/api/matches/{matchId}/info", matchId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.errorCode").value("MATCH_008"));
  }
}