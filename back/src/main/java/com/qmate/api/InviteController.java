package com.qmate.api;

import com.qmate.domain.match.model.request.InviteCodeValidationRequest;
import com.qmate.domain.match.model.response.InviteCodeValidationResponse;
import com.qmate.domain.match.service.MatchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/invites")
@RequiredArgsConstructor
public class InviteController {

  private final MatchService matchService;

  @PostMapping("/validate")
  public ResponseEntity<InviteCodeValidationResponse> validateInviteCode(
      @RequestBody @Valid InviteCodeValidationRequest request
  ){
    InviteCodeValidationResponse response = matchService.validateInviteCode(request.getInviteCode());
    return ResponseEntity.ok(response);
  }

}
