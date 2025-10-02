package com.qmate.api.match;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.qmate.AuthTestUtils;
import com.qmate.SecuritySliceTestConfig;
import com.qmate.api.MatchController;
import com.qmate.domain.match.model.response.DetachedMatchStatusResponse;
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
class MatchControllerDetachedStatusTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private MatchService matchService;

  @Test
  @DisplayName("복구 가능 매칭 조회 성공: 매칭이 있을 때 true와 matchId를 응답한다")
  void getDetachedMatchStatus_success_whenMatchExists() throws Exception {
    // given: 서비스가 '매칭 있음' DTO를 반환하는 상황
    Long requesterId = 1L;
    var fakeResponse = new DetachedMatchStatusResponse(true, 123L);
    given(matchService.getDetachedMatchStatus(anyLong())).willReturn(fakeResponse);

    // expect: API를 호출하면, 200 OK 응답과 DTO 내용이 정확히 와야 함
    mockMvc.perform(get("/api/matches/detached-status")
            .with(AuthTestUtils.userPrincipal(requesterId)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.hasDetachedMatch").value(true))
        .andExpect(jsonPath("$.matchId").value(123L));
  }

  @Test
  @DisplayName("복구 가능 매칭 조회 성공: 매칭이 없을 때 false와 null을 응답한다")
  void getDetachedMatchStatus_success_whenMatchDoesNotExist() throws Exception {
    // given: 서비스가 '매칭 없음' DTO를 반환하는 상황
    Long requesterId = 2L;
    var fakeResponse = new DetachedMatchStatusResponse(false, null);
    given(matchService.getDetachedMatchStatus(anyLong())).willReturn(fakeResponse);

    // expect
    mockMvc.perform(get("/api/matches/detached-status")
            .with(AuthTestUtils.userPrincipal(requesterId)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.hasDetachedMatch").value(false))
        .andExpect(jsonPath("$.matchId").isEmpty());
  }
}
