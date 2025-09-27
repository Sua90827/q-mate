package com.qmate.api;

import com.qmate.common.constants.match.MatchConstants;
import com.qmate.domain.match.model.request.MatchCreationRequest;
import com.qmate.domain.match.model.request.MatchJoinRequest;
import com.qmate.domain.match.model.request.MatchUpdateRequest;
import com.qmate.domain.match.model.response.MatchActionResponse;
import com.qmate.domain.match.model.response.MatchCreationResponse;
import com.qmate.domain.match.model.response.MatchInfoResponse;
import com.qmate.domain.match.model.response.MatchJoinResponse;
import com.qmate.domain.match.model.response.MatchMembersResponse;
import com.qmate.domain.match.service.MatchService;
import com.qmate.security.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/matches")
@RequiredArgsConstructor
public class MatchController {

  private final MatchService matchService;

  //매칭 생성(초대 코드 발급)
  @PostMapping
  public ResponseEntity<MatchCreationResponse> createMatch(
      @RequestBody @Valid MatchCreationRequest request,
      @AuthenticationPrincipal UserPrincipal principal
  ) {

    Long currentUserId = principal.userId();
    MatchCreationResponse response = matchService.createMatch(request, currentUserId);
    // 5. API 명세서에 따라 201 Created 상태 코드와 함께 응답 반환
    return ResponseEntity.status(HttpStatus.CREATED).body(response);

  }

  //매칭 참여(초대 코드 입력)
  @PostMapping("/join")
  public ResponseEntity<MatchJoinResponse> joinMatch(
      @RequestBody @Valid MatchJoinRequest request,
      @AuthenticationPrincipal UserPrincipal principal
  ) {
    Long currentUserId = principal.userId();
    MatchJoinResponse response = matchService.joinMatch(request, currentUserId);
    return ResponseEntity.ok(response); // 200 OK 상태 코드와 함께 응답


  }

  /**
   * 특정 매칭의 상세 정보를 조회합니다.
   *
   * @param matchId URL 경로에서 받아온 매칭 ID
   * @return 200 OK 상태 코드와 함께 매칭 정보 DTO를 반환
   */
  @GetMapping("{matchId}")
  public ResponseEntity<MatchInfoResponse> getMatchInfo(
      @PathVariable Long matchId,
      @AuthenticationPrincipal UserPrincipal principal
  ) {
    Long currentUserId = principal.userId();
    MatchInfoResponse response = matchService.getMatchInfo(matchId, currentUserId);
    return ResponseEntity.ok(response);
  }

  //특정 매칭의 구성원 목록(상세정보)을 조회.
  @GetMapping("/{matchId}/members")
  public ResponseEntity<MatchMembersResponse> getMatchMembers(
      @PathVariable Long matchId,
      @AuthenticationPrincipal UserPrincipal principal
  ) {
    Long currentUserId = principal.userId();
    MatchMembersResponse response = matchService.getMatchMembers(matchId, currentUserId);
    return ResponseEntity.ok(response);
  }

  /**
   * 특정 매칭의 정보를 업데이트(기념일, 질문 받는 시간 등)
   *
   * @param matchId URL 경로에서 받아온 매칭 ID
   * @param request 업데이트할 정보가 담긴 DTO
   * @return 200 OK 상태 코드와 함께 성공 메시지를 반환
   */
  @PatchMapping("/{matchId}/info")
  public ResponseEntity<Void> updateMatchInfo(
      @PathVariable Long matchId,
      @RequestBody @Valid MatchUpdateRequest request,
      @AuthenticationPrincipal UserPrincipal principal
  ) {
    Long currentUserId = principal.userId();
    matchService.updateMatchInfo(matchId, currentUserId, request);
    // 성공적으로 처리되었지만, 별도의 응답 본문은 없다는 의미로 204 No Content를 반환
    return ResponseEntity.noContent().build();
  }
  //매칭 연결을 끊습니다.(2주간의 복구 유예 기간 시작)
  @PostMapping("/{matchId}/disconnect")
  public ResponseEntity<MatchActionResponse> disconnectMatch(
      @PathVariable Long matchId,
      @AuthenticationPrincipal UserPrincipal principal
  ){
    Long currentUserId = principal.userId();
    matchService.disconnectMatch(matchId, currentUserId);

    return ResponseEntity.ok(new MatchActionResponse(MatchConstants.DISCONNECT_SUCCESS_MESSAGE));
  }
}
