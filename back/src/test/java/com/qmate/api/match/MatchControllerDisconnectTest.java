package com.qmate.api.match;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.qmate.AuthTestUtils; // ◀◀◀ 1. AuthTestUtils import
import com.qmate.SecuritySliceTestConfig; // ◀◀◀ 2. SecuritySliceTestConfig import
import com.qmate.api.MatchController;
import com.qmate.domain.match.service.MatchService;
import com.qmate.exception.custom.matchinstance.MatchForbiddenException;
import com.qmate.exception.custom.matchinstance.MatchNotFoundException;
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
@AutoConfigureMockMvc // 이 어노테이션은 그대로 두거나, addFilters=false를 제거합니다.
@Import(SecuritySliceTestConfig.class) // ◀◀◀ 3. SecurityConfig 대신, 테스트 전용 설정을 import
class MatchControllerDisconnectTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private MatchService matchService;


  @Test
  @DisplayName("연결 끊기 성공 시 200 OK와 성공 메시지를 응답한다")
  void disconnectMatch_success_200ok() throws Exception {
    // given: 서비스가 정상적으로 동작하는 상황
    Long matchId = 100L;
    Long requesterId = 1L; // 테스트용 사용자 ID
    willDoNothing().given(matchService).disconnectMatch(anyLong(), anyLong());

    // expect: API를 호출하면, 200 OK 응답과 메시지가 와야 함
    mockMvc.perform(post("/api/matches/{matchId}/disconnect", matchId)
            .with(AuthTestUtils.userPrincipal(requesterId))) // ◀◀◀ 4. .with()로 가짜 사용자 주입!
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").exists());
  }
  @Test
  @DisplayName("연결 끊기 실패: 존재하지 않는 매칭 ID 요청 시 404 Not Found 응답")
  void disconnectMatch_fail_404notFound() throws Exception {
    Long nonExistentMatchId = 404L;
    willThrow(new MatchNotFoundException())
        .given(matchService).disconnectMatch(anyLong(), anyLong());

    mockMvc.perform(post("/api/matches/{matchId}/disconnect", nonExistentMatchId)
            .with(AuthTestUtils.userPrincipal(1L)))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.errorCode").value("MATCH_002"));
  }
  @Test
  @DisplayName("연결 끊기 실패: 권한 없는 접근 시 403 Forbidden 응답")
  void disconnectMatch_fail_403forbidden() throws Exception {
    // given: 서비스가 MatchForbiddenException을 던지는 상황
    Long nonExistentMatchId = 100L;
    willThrow(new MatchForbiddenException())
        .given(matchService).disconnectMatch(anyLong(), anyLong());

    // expect: 403 Forbidden 응답과 MATCH_008 에러 코드가 와야 함
    mockMvc.perform(post("/api/matches/{matchId}/disconnect", nonExistentMatchId)
            .with(AuthTestUtils.userPrincipal(1L)))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.errorCode").value("MATCH_008"));
  }

  @Test
  @DisplayName("연결 끊기 실패: 상태 불일치 시 409 Conflict 응답")
  void disconnectMatch_fail_409conflict() throws Exception {
    // given: 서비스가 MatchStateConflictException을 던지는 상황
    Long nonExistentMatchId = 100L;
    willThrow(new MatchStateConflictException())
        .given(matchService).disconnectMatch(anyLong(), anyLong());

    // expect: 409 Conflict 응답과 MATCH_009 에러 코드가 와야 함
    mockMvc.perform(post("/api/matches/{matchId}/disconnect", nonExistentMatchId)
        .with(AuthTestUtils.userPrincipal(1L)))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.errorCode").value("MATCH_009"));
  }
}