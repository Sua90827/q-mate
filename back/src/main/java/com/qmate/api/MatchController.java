package com.qmate.api;

import com.qmate.common.constants.match.MatchConstants;
import com.qmate.domain.match.model.request.MatchCreationRequest;
import com.qmate.domain.match.model.request.MatchJoinRequest;
import com.qmate.domain.match.model.request.MatchUpdateRequest;
import com.qmate.domain.match.model.response.DetachedMatchStatusResponse;
import com.qmate.domain.match.model.response.LockStatusResponse;
import com.qmate.domain.match.model.response.MatchActionResponse;
import com.qmate.domain.match.model.response.MatchCreationResponse;
import com.qmate.domain.match.model.response.MatchInfoResponse;
import com.qmate.domain.match.model.response.MatchJoinResponse;
import com.qmate.domain.match.model.response.MatchMembersResponse;
import com.qmate.domain.match.service.MatchService;
import com.qmate.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/matches")
@RequiredArgsConstructor
@Tag(name = "Match", description = "매칭 관련 API")
public class MatchController {

  private final MatchService matchService;

  //매칭 생성(초대 코드 발급)
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping
  @Operation(
      summary = "매칭 초대코드 발급",
      description = MatchConstants.CREATE_MATCH_MD
  )
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
  @Operation(
      summary = "매칭 참여 초대코드 입력",
      description = MatchConstants.JOIN_MATCH_MD
  )
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
  @Operation(
      summary = "매칭 정보 조회",
      description = MatchConstants.GET_MATCH_INFO_MD
  )
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
  @Operation(
      summary = "특정 매칭의 구성원 목록 조회",
      description = MatchConstants.GET_MATCH_MEMBERS_MD
  )
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
  @Operation(
      summary = "특정 매칭의 정보를 업데이트 합니다.",
      description = MatchConstants.UPDATE_MATCH_INFO_MD
  )
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
  @Operation(
      summary = "매칭 연결을 끊습니다.",
      description = MatchConstants.DISCONNECT_MATCH_MD
  )
  public ResponseEntity<MatchActionResponse> disconnectMatch(
      @PathVariable Long matchId,
      @AuthenticationPrincipal UserPrincipal principal
  ){
    Long currentUserId = principal.userId();
    matchService.disconnectMatch(matchId, currentUserId);

    return ResponseEntity.ok(new MatchActionResponse(MatchConstants.DISCONNECT_SUCCESS_MESSAGE));
  }
  //매칭 연결 복구
  @PostMapping("/{matchId}/restore")
  @Operation(
      summary = "매칭 연결 복구",
      description = MatchConstants.RESTORE_MATCH_MD
  )
  public ResponseEntity<MatchActionResponse> restoreMatch(
      @PathVariable Long matchId,
      @AuthenticationPrincipal UserPrincipal principal
  ){
    Long currentUserId = principal.userId();
    boolean isFullyRestored = matchService.restoreMatch(matchId, currentUserId);

    //서비스의 결과에 따른 메세지를 선태
    String message = isFullyRestored
        ? MatchConstants.RESTORE_SUCCESS_MESSAGE
        : MatchConstants.RESTORE_AGREED_AWAITING_PARTNER_MESSAGE;

    return ResponseEntity.ok(new MatchActionResponse(message));
  }
  //현재 로그인한 사용자의 초대 코드 입력 잠금 상태를 조회
  @GetMapping("/lock-status")
  @Operation(
      summary = "잠금 상태 조회",
      description = MatchConstants.GET_LOCK_STATUS_MD
  )
  public ResponseEntity<LockStatusResponse> getLockStatus(
      @AuthenticationPrincipal UserPrincipal principal
  ){
    LockStatusResponse response = matchService.getLockStatus(principal.userId());
    return ResponseEntity.ok(response);
  }
  //현재 로그인한 사용자가 복구 가능한 '연결 끊김' 상태의 매칭을 가지고 있는지 조회
  @GetMapping("/detached-status")
  @Operation(
      summary = "복구 가능한 '연결 끊김' 상태의 매칭 조회",
      description = MatchConstants.GET_DETACHED_STATUS_MD
  )
  public ResponseEntity<DetachedMatchStatusResponse> getDetachedMatchStatus(
      @AuthenticationPrincipal UserPrincipal principal
  ){
    DetachedMatchStatusResponse response = matchService.getDetachedMatchStatus(principal.userId());
    return ResponseEntity.ok(response);
  }

// 14일 이상 연결 끊기 테스트 컨트롤러
//  @PostMapping("/run-inactive-check")
//  public ResponseEntity<String> runInactiveCheck() {
//    matchService.disconnectInactiveMatches();
//    return ResponseEntity.ok("비활성 매칭 체크 스케줄러를 수동으로 실행했습니다.");
//  }
// 자동 delete모드 스케줄링 테스트 코드
//  @PostMapping("/run-finalize-check")
//  public ResponseEntity<String> runFinalizeCheck() {
//    matchService.finalizeExpiredMatches();
//    return ResponseEntity.ok("유예기간 만료 매칭 정리 스케줄러를 수동으로 실행했습니다.");
//  }
}
