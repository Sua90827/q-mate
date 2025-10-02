package com.qmate.api.match;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qmate.AuthTestUtils;
import com.qmate.SecuritySliceTestConfig;
import com.qmate.api.MatchController;
import com.qmate.domain.match.Match;
import com.qmate.domain.match.RelationType;
import com.qmate.domain.match.model.request.MatchJoinRequest;
import com.qmate.domain.match.model.request.MatchUpdateRequest;
import com.qmate.domain.match.model.response.MatchInfoResponse;
import com.qmate.domain.match.model.response.MatchMembersResponse;
import com.qmate.domain.match.service.MatchService;
import com.qmate.exception.BusinessGlobalException;
import com.qmate.exception.MatchErrorCode;
import com.qmate.exception.custom.matchinstance.MatchForbiddenException;
import java.time.LocalDateTime;
import java.util.List;
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
@AutoConfigureMockMvc
@Import(SecuritySliceTestConfig.class)
class MatchControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private MatchService matchService;

  @Test
  @DisplayName("매칭 정보 업데이트 성공 시 204 No content 응답")
  void updateMatchInfo_success_204noContent() throws Exception {
    Long matchId = 3L;
    Long requesterId = 1L;
    var request = new MatchUpdateRequest();
    request.setDailyQuestionHour(20);

    willDoNothing().given(matchService)
        .updateMatchInfo(matchId, requesterId, request);

    mockMvc.perform(patch("/api/matches/{matchId}/info", matchId)
            .with(AuthTestUtils.userPrincipal(requesterId))
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("매칭 정보 업데이트 실패: 유효성 검증 실패 시 400 Bad Request 응답")
  void updateMatchInfo_fail_400badRequest() throws Exception {
    Long matchId = 3L;
    Long requesterId = 1L;
    var badRequest = new MatchUpdateRequest();
    badRequest.setDailyQuestionHour(25);

    mockMvc.perform(patch("/api/matches/{matchId}/info", matchId)
            .with(AuthTestUtils.userPrincipal(requesterId))
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(badRequest)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("매칭 멤버 상세 조회 실패: 권한 없는 접근 시 403 Forbidden 응답")
  void getMatchMembers_fail_403forbidden() throws Exception {
    Long matchId = 4L;
    Long requesterId = 2L;

    willThrow(new MatchForbiddenException())
        .given(matchService).getMatchMembers(matchId, requesterId);

    mockMvc.perform(get("/api/matches/{matchId}/members", matchId)
            .with(AuthTestUtils.userPrincipal(requesterId)))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.errorCode").value("MATCH_008"));
  }

  @Test
  @DisplayName("매칭 멤버 상세 조회 성공 시 200 OK와 DTO를 응답한다")
  void getMatchMembers_success_200ok() throws Exception {
    Long matchId = 1L;
    Long requesterId = 1L;

    var fakeResponse = new MatchMembersResponse(
        Match.builder().id(matchId).members(List.of()).build(),
        requesterId
    );

    given(matchService.getMatchMembers(matchId, requesterId)).willReturn(fakeResponse);

    mockMvc.perform(get("/api/matches/{matchId}/members", matchId)
            .with(AuthTestUtils.userPrincipal(requesterId)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.matchId").value(matchId));
  }

  @Test
  @DisplayName("매칭 정보 조회 성공 시 200 OK와 DTO를 응답한다")
  void getMatchInfo_success_200ok() throws Exception {
    Long matchId = 1L;
    Long requesterId = 3L;

    var fakeResponse = new MatchInfoResponse(
        Match.builder()
            .id(matchId)
            .relationType(RelationType.COUPLE)
            .startDate(LocalDateTime.now())
            .members(List.of())
            .build(),
        requesterId
    );

    given(matchService.getMatchInfo(matchId, requesterId)).willReturn(fakeResponse);

    mockMvc.perform(get("/api/matches/{matchId}", matchId)
            .with(AuthTestUtils.userPrincipal(requesterId)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.matchId").value(matchId))
        .andExpect(jsonPath("$.relationType").value("COUPLE"));
  }

  @Test
  @DisplayName("매칭 정보 조회 실패: 권한 없는 접근 시 403 Forbidden 응답")
  void getMatchInfo_fail_403forbidden() throws Exception {
    Long matchId = 2L;
    Long requesterId = 5L;

    willThrow(new MatchForbiddenException())
        .given(matchService).getMatchInfo(matchId, requesterId);

    mockMvc.perform(get("/api/matches/{matchId}", matchId)
            .with(AuthTestUtils.userPrincipal(requesterId)))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.errorCode").value("MATCH_008"));
  }

  @Test
  @DisplayName("매칭 참여 실패: 5회 시도 잠금 시 403 Forbidden 응답")
  void joinMatch_fail_whenLocked() throws Exception {
    Long requesterId = 7L;
    var request = new MatchJoinRequest();
    request.setInviteCode("000000");

    // InviteAttemptLockedException이 BusinessGlobalException을 상속하고,
    // MatchErrorCode.inviteAttemptLocked()를 넘겨줘야 함
    willThrow(new BusinessGlobalException(MatchErrorCode.inviteAttemptLocked()))
        .given(matchService).joinMatch(any(MatchJoinRequest.class), eq(requesterId));

    mockMvc.perform(post("/api/matches/join")
            .with(AuthTestUtils.userPrincipal(requesterId))
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isForbidden())              // 403
        .andExpect(jsonPath("$.errorCode").value(
            "MATCH_007")) // JSON 응답 필드명이 $.errorCode가 아니라 $.code일 가능성 있음
        .andExpect(jsonPath("$.message").value("초대 코드 입력 5회 실패하여 24시간 동안 시도할 수 없습니다."));
  }

}
