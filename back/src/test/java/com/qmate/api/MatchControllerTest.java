package com.qmate.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qmate.config.SecurityConfig;
import com.qmate.domain.match.model.request.MatchJoinRequest;
import com.qmate.domain.match.service.MatchService;
import com.qmate.exception.custom.InviteAttemptLockedException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = MatchController.class)
@Import(SecurityConfig.class) // SecurityConfig를 테스트에 포함
class MatchControllerTest {

  @Autowired
  private MockMvc mockMvc; // API 요청을 보낼 가짜 웹 브라우저

  @Autowired
  private ObjectMapper objectMapper; // Java 객체를 JSON으로 변환

  @MockitoBean // 가짜 MatchService를 Spring에 등록
  private MatchService matchService;

  @Test
  @DisplayName("매칭 참여 실패: 5회 시도 잠금 시 403 Forbidden 응답")
  void joinMatch_fail_whenLocked() throws Exception {
    // given: matchService가 InviteAttemptLockedException을 던지도록 설정
    var request = new MatchJoinRequest();
    request.setInviteCode("000000");

    willThrow(new InviteAttemptLockedException())
        .given(matchService).joinMatch(any(MatchJoinRequest.class), anyLong());

    // expect: API를 호출하면, 403 Forbidden 응답과 MATCH_007 에러 코드가 와야 함
    mockMvc.perform(post("/api/matches/join")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.errorCode").value("MATCH_007"));
  }
}

