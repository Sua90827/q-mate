package com.qmate.api;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.qmate.AuthTestUtils;
import com.qmate.SecuritySliceTestConfig;
import com.qmate.domain.match.service.MatchService;
import com.qmate.exception.custom.matchinstance.MatchForbiddenException;
import com.qmate.exception.custom.matchinstance.MatchNotFoundException;
import com.qmate.exception.custom.matchinstance.MatchRecoveryExpiredException;
import com.qmate.exception.custom.matchinstance.MatchStateConflictException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = MatchController.class)
@AutoConfigureMockMvc
@Import(SecuritySliceTestConfig.class)
class MatchControllerRestoreTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private MatchService matchService;

  @Test
  @DisplayName("연결 복구 성공 시 200 OK와 성공 메시지를 응답한다")
  void restoreMatch_success_200ok() throws Exception {
    // given
    Long matchId = 100L;
    willDoNothing().given(matchService).restoreMatch(anyLong(), anyLong());

    // expect
    mockMvc.perform(post("/api/matches/{matchId}/restore", matchId)
            .with(AuthTestUtils.userPrincipal(1L)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("매칭이 성공적으로 복구되었습니다."));
  }

  @Test
  @DisplayName("연결 복구 실패: 복구 기간 만료 시 409 Conflict 응답")
  void restoreMatch_fail_409expired() throws Exception {
    // given
    Long matchId = 100L;
    willThrow(new MatchRecoveryExpiredException())
        .given(matchService).restoreMatch(anyLong(), anyLong());

    // expect
    mockMvc.perform(post("/api/matches/{matchId}/restore", matchId)
            .with(AuthTestUtils.userPrincipal(1L)))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.errorCode").value("MATCH_010"));
  }

  // (이하 MatchControllerDisconnectTest와 유사하게, 404, 403, 409(상태) 실패 케이스도 추가하면 좋습니다.)
}