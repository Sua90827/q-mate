package com.qmate.api.match;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.qmate.AuthTestUtils;
import com.qmate.SecuritySliceTestConfig;
import com.qmate.api.MatchController;
import com.qmate.domain.match.model.response.LockStatusResponse;
import com.qmate.domain.match.service.MatchService;
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
public class MatchControllerInvitedCodeLockUserTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private MatchService matchService;

  @Test
  @DisplayName("잠금 상태 조회 성공: 잠겨있을 때 true와 남은 시간을 응답한다")
  void getLockStatus_success_whenLocked() throws Exception {
    // given: 서비스가 '잠겨있음' DTO를 반환하는 상황
    Long requesterId = 1L;
    var fakeResponse = new LockStatusResponse(true, 3600L);
    given(matchService.getLockStatus(anyLong())).willReturn(fakeResponse);

    // expect: API를 호출하면, 200 OK 응답과 DTO 내용이 정확히 와야 함
    mockMvc.perform(get("/api/matches/lock-status")
            .with(AuthTestUtils.userPrincipal(requesterId)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.locked").value(true))
        .andExpect(jsonPath("$.remainingSeconds").value(3600L));
  }

  @Test
  @DisplayName("잠금 상태 조회 성공: 잠겨있지 않을 때 false와 0을 응답한다")
  void getLockStatus_success_whenNotLocked() throws Exception {
    // given: 서비스가 '잠겨있지 않음' DTO를 반환하는 상황
    Long requesterId = 2L;
    var fakeResponse = new LockStatusResponse(false, 0L);
    given(matchService.getLockStatus(anyLong())).willReturn(fakeResponse);

    // expect
    mockMvc.perform(get("/api/matches/lock-status")
            .with(AuthTestUtils.userPrincipal(requesterId)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.locked").value(false))
        .andExpect(jsonPath("$.remainingSeconds").value(0L));
  }
}
