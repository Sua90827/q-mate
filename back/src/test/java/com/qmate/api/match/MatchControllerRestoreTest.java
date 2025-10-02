package com.qmate.api.match;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.qmate.AuthTestUtils;
import com.qmate.SecuritySliceTestConfig;
import com.qmate.api.MatchController;
import com.qmate.common.constants.match.MatchConstants;
import com.qmate.domain.match.service.MatchService;
import com.qmate.exception.custom.matchinstance.MatchRecoveryExpiredException;
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
  @DisplayName("연결 복구 성공: 상대 동의를 기다려야 할 때, '대기' 메시지를 응답한다")
  void restoreMatch_success_awaitingPartner() throws Exception {
    // given: 서비스가 '최종 복구 아님'을 의미하는 false를 반환하는 상황
    Long matchId = 100L;
    given(matchService.restoreMatch(anyLong(), anyLong())).willReturn(false);

    // expect: 200 OK 응답과 함께 '상대방 동의 대기' 메시지가 와야 함
    mockMvc.perform(post("/api/matches/{matchId}/restore", matchId)
            .with(AuthTestUtils.userPrincipal(1L)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value(MatchConstants.RESTORE_AGREED_AWAITING_PARTNER_MESSAGE));
  }

  @Test
  @DisplayName("연결 복구 성공: 최종 복구 완료 시, '성공' 메시지를 응답한다")
  void restoreMatch_success_fullyRestored() throws Exception {
    // given: 서비스가 '최종 복구 성공'을 의미하는 true를 반환하는 상황
    Long matchId = 100L;
    given(matchService.restoreMatch(anyLong(), anyLong())).willReturn(true);

    // expect: 200 OK 응답과 함께 '성공' 메시지가 와야 함
    mockMvc.perform(post("/api/matches/{matchId}/restore", matchId)
            .with(AuthTestUtils.userPrincipal(1L)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value(MatchConstants.RESTORE_SUCCESS_MESSAGE));
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