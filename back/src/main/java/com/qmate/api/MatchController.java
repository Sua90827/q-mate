package com.qmate.api;

import com.qmate.domain.match.model.request.MatchCreationRequest;
import com.qmate.domain.match.model.request.MatchJoinRequest;
import com.qmate.domain.match.model.response.MatchCreationResponse;
import com.qmate.domain.match.model.response.MatchJoinResponse;
import com.qmate.domain.match.service.MatchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    Long currentUserId = 1L;
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
    Long currentUserId = 2L;
    // Long currentUserId = userDetails.getUser().getUserId();

    MatchJoinResponse response = matchService.joinMatch(request, currentUserId);

    return ResponseEntity.ok(response); // 200 OK 상태 코드와 함께 응답


  }
}
