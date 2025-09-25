package com.qmate.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qmate.domain.match.RelationType;
import com.qmate.domain.match.model.request.MatchJoinRequest;
import com.qmate.domain.match.model.response.MatchInfoResponse;
import com.qmate.domain.match.Match;
import com.qmate.domain.match.model.response.MatchMembersResponse;
import com.qmate.domain.match.service.MatchService;

import com.qmate.exception.custom.matchinstance.InviteAttemptLockedException;
import com.qmate.exception.custom.matchinstance.MatchForbiddenException;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = MatchController.class)
@AutoConfigureMockMvc(addFilters = false) // SecurityConfig를 테스트에 포함
class MatchControllerTest {

  @Autowired
  private MockMvc mockMvc; // API 요청을 보낼 가짜 웹 브라우저

  @Autowired
  private ObjectMapper objectMapper; // Java 객체를 JSON으로 변환

  @MockitoBean // 가짜 MatchService를 Spring에 등록
  private MatchService matchService;


  @Test
  @DisplayName("매칭 멤버 상세 조회 실패: 권한 없는 접근 시 403 Forbidden 응답")
  void getMatchMembers_fail_403forbidden() throws Exception {
    // given: 서비스가 MatchForbiddenException을 던지는 상황을 가정
    Long matchId = 4L;

    // "만약 matchService의 getMatchMembers가 어떤 ID로든 호출되면, 이 예외를 던져라"
    willThrow(new MatchForbiddenException())
        .given(matchService).getMatchMembers(anyLong(), anyLong());

    // expect: API를 호출하면, 403 Forbidden 응답과 MATCH_008 에러 코드가 와야 함
    mockMvc.perform(get("/api/matches/{matchId}/members", matchId))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.errorCode").value("MATCH_008"));
  }

  @Test
  @DisplayName("매칭 멤버 상세 조회 성공 시 200 OK와 DTO를 응답한다")
  void getMatchMembers_success_200ok() throws Exception {
    // given: 서비스가 정상적으로 MatchMembersResponse DTO를 반환하는 상황
    Long matchId = 1L;
    Long requesterId = 1L;

    // 서비스가 반환할 가짜 응답 DTO를 미리 만들어둠
    var fakeResponse = new MatchMembersResponse(
        Match.builder().id(matchId).members(List.of()).build(),
        requesterId
    );

    given(matchService.getMatchMembers(anyLong(), anyLong())).willReturn(fakeResponse);

    // expect: API를 호출하면, 200 OK 응답과 DTO 내용이 정확히 와야 함
    mockMvc.perform(get("/api/matches/{matchId}/members", matchId)) // GET 요청
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.matchId").value(matchId));
  }

  @Test
  @DisplayName("매칭 정보 조회 성공 시 200 OK와 DTO를 응답한다")
  void getMatchInfo_success_200ok() throws Exception {
    // given: 서비스가 정상적으로 MatchInfoResponse DTO를 반환하는 상황
    Long matchId = 1L;
    Long requesterId = 3L;

    // 서비스가 반환할 가짜 응답 DTO를 미리 만들어둠
    var fakeResponse = new MatchInfoResponse(
        Match.builder()
            .id(matchId)
            .relationType(RelationType.COUPLE)
            .startDate(LocalDateTime.now())
            .members(List.of()) // 테스트에서는 멤버 리스트가 비어있어도 괜찮습니다.
            .build(),
        requesterId
    );

    // matchService.getMatchInfo가 어떤 ID로든 호출되면, 위에서 만든 가짜 응답을 반환하도록 설정
    given(matchService.getMatchInfo(anyLong(), anyLong())).willReturn(fakeResponse);

    // expect: API를 호출하면, 200 OK 응답과 DTO 내용이 정확히 와야 함
    mockMvc.perform(get("/api/matches/{matchId}", matchId)) // GET 요청
        .andExpect(status().isOk()) // 1. HTTP 상태 코드가 200 OK인지 검증
        .andExpect(jsonPath("$.matchId").value(matchId)) // 2. 응답 JSON의 필드 검증
        .andExpect(jsonPath("$.relationType").value("COUPLE"));
  }

  @Test
  @DisplayName("매칭 정보 조회 실패: 권한 없는 접근 시 403 Forbidden 응답")
  void getMatchInfo_fail_403forbidden() throws Exception {
    // given: 서비스가 MatchForbiddenException을 던지는 상황
    Long matchId = 2L;

    // "만약 matchService의 getMatchInfo가 어떤 ID로든 호출되면, 이 예외를 던져라"
    willThrow(new MatchForbiddenException())
        .given(matchService).getMatchInfo(anyLong(), anyLong());

    // expect: API를 호출하면, 403 Forbidden 응답과 MATCH_008 에러 코드가 와야 함
    mockMvc.perform(get("/api/matches/{matchId}", matchId))
        .andExpect(status().isForbidden()) // 1. HTTP 상태 코드가 403 Forbidden인지 검증
        .andExpect(jsonPath("$.errorCode").value("MATCH_008")); // 2. 응답 JSON의 에러 코드 검증
  }

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

