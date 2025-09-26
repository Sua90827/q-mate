package com.qmate.api;

import com.qmate.domain.match.model.request.MatchCreationRequest;
import com.qmate.domain.match.model.request.MatchJoinRequest;
import com.qmate.domain.match.model.request.MatchUpdateRequest;
import com.qmate.domain.match.model.response.MatchCreationResponse;
import com.qmate.domain.match.model.response.MatchInfoResponse;
import com.qmate.domain.match.model.response.MatchJoinResponse;
import com.qmate.domain.match.model.response.MatchMembersResponse;
import com.qmate.domain.match.service.MatchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
      @RequestBody @Valid MatchCreationRequest request
      // @AuthenticationPrincipal UserDetailsImpl userDetails => spring Security가 로그인한 사용자의 정보를 자동으로 주입
  ) {
    // (임시) 로그인 기능 구현 전이므로, 사용자 ID를 1L로 가정
    Long currentUserId = 3L;
    // Long currentUserId = userDetails.getUser().getUserId(); // 나중에는 이렇게 ID를 가져올 예정.

    MatchCreationResponse response = matchService.createMatch(request, currentUserId);

    // 5. API 명세서에 따라 201 Created 상태 코드와 함께 응답 반환
    return ResponseEntity.status(HttpStatus.CREATED).body(response);

  }

  //매칭 참여(초대 코드 입력)
  @PostMapping("/join")
  public ResponseEntity<MatchJoinResponse> joinMatch(
      @RequestBody @Valid MatchJoinRequest request
      // @AuthenticationPrincipal UserDetailsImpl userDetails //  나중에 로그인 기능 연동
  ) {
    // 임시로 참여자 ID를 2L로 가정 (1L은 이미 방을 만들었으므로)
    Long currentUserId = 4L;
    // Long currentUserId = userDetails.getUser().getUserId();

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
      @PathVariable Long matchId
      // @AuthenticationPrincipal UserDetailsImpl userDetails //  나중에 로그인 기능 연동
  ) {
    Long currentUserId = 3L;
    // 임시로 사용자 Id를 1L로 가정 Long currentUserId = userDetails.getUser().getId();
    MatchInfoResponse response = matchService.getMatchInfo(matchId, currentUserId);

    return ResponseEntity.ok(response);
  }
  //특정 매칭의 구성원 목록(상세정보)을 조회.
  @GetMapping("/{matchId}/members")
  public ResponseEntity<MatchMembersResponse> getMatchMembers(
      @PathVariable Long matchId
      // @AuthenticationPrincipal UserDetailsImpl userDetails
  ){
    Long currentUserId = 3L;
    // Long currentUserId = userDetails.getUser().getId();

    MatchMembersResponse response = matchService.getMatchMembers(matchId, currentUserId);
    return ResponseEntity.ok(response);
  }
  /**특정 매칭의 정보를 업데이트(기념일, 질문 받는 시간 등)
   * @param matchId URL 경로에서 받아온 매칭 ID
   * @param request 업데이트할 정보가 담긴 DTO
   * @return 200 OK 상태 코드와 함께 성공 메시지를 반환
   */
   @PatchMapping("/{matchId}/info")
  public ResponseEntity<Void> updateMatchInfo(
      @PathVariable Long matchId,
       @RequestBody @Valid MatchUpdateRequest request
       // @AuthenticationPrincipal UserDetailsImpl userDetails
   ){
     // 임시로 요청을 보낸 사용자의 ID를 3L로 가정합니다.
     Long currentUserId = 3L;
     // Long currentUserId = userDetails.getUser().getId();

     matchService.updateMatchInfo(matchId, currentUserId,request);

     // 성공적으로 처리되었지만, 별도의 응답 본문은 없다는 의미로 204 No Content를 반환
     return ResponseEntity.noContent().build();
   }
}
