package com.qmate.api.match;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qmate.AuthTestUtils;
import com.qmate.SecuritySliceTestConfig;
import com.qmate.api.InviteController;
import com.qmate.domain.match.model.request.InviteCodeValidationRequest;
import com.qmate.domain.match.model.response.InviteCodeValidationResponse;
import com.qmate.domain.match.service.MatchService;
import com.qmate.exception.custom.matchinstance.InviteCodeExpiredException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = InviteController.class)
@AutoConfigureMockMvc
@Import(SecuritySliceTestConfig.class)
class InviteControllerValidateTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private MatchService matchService;

  @Test
  @DisplayName("초대 코드 유효성 검증 성공 시 200 OK와 결과를 응답한다")
  void validateInviteCode_success_200ok() throws Exception {
    // given
    var request = new InviteCodeValidationRequest();
    request.setInviteCode("123456");
    var fakeResponse = new InviteCodeValidationResponse(true, "파트너닉네임");

    given(matchService.validateInviteCode(any(String.class))).willReturn(fakeResponse);

    // expect
    // ▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼ 이 부분의 괄호 위치가 수정되었습니다 ▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼
    mockMvc.perform(post("/api/invites/validate")
            .with(AuthTestUtils.userPrincipal(1L))
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)) // ◀◀◀ 요청 만들기가 여기서 끝나고
        ) // ◀◀◀ perform()의 닫는 괄호가 여기에 위치해야 합니다.
        .andExpect(status().isOk()) // ◀◀◀ 그 다음에 응답을 검증합니다.
        .andExpect(jsonPath("$.valid").value(true))
        .andExpect(jsonPath("$.partnerNickname").value("파트너닉네임"));
    // ▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲
  }

  @Test
  @DisplayName("초대 코드 유효성 검증 실패: 코드가 유효하지 않을 시 400 Bad Request 응답")
  void validateInviteCode_fail_400badRequest() throws Exception {
    // given
    var request = new InviteCodeValidationRequest();
    request.setInviteCode("000000");

    willThrow(new InviteCodeExpiredException())
        .given(matchService).validateInviteCode(any(String.class));

    // expect
    // ▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼ 이 부분의 괄호 위치가 수정되었습니다 ▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼
    mockMvc.perform(post("/api/invites/validate")
            .with(AuthTestUtils.userPrincipal(1L))
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request))
        )
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errorCode").value("MATCH_004"));
    // ▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲
  }
}