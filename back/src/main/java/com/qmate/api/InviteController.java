package com.qmate.api;

import com.qmate.common.constants.match.MatchConstants;
import com.qmate.domain.match.model.request.InviteCodeValidationRequest;
import com.qmate.domain.match.model.response.InviteCodeValidationResponse;
import com.qmate.domain.match.service.MatchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "InviteCodeValidation", description = "초대코드 유효성 검증")
public class InviteController {

  private final MatchService matchService;

  @PostMapping("/validate")
  @Operation(
      summary = "초대코드 유효성 검증",
      description = MatchConstants.VALIDATE_INVITE_CODE_MD
  )
  public ResponseEntity<InviteCodeValidationResponse> validateInviteCode(
      @RequestBody @Valid InviteCodeValidationRequest request
  ){
    InviteCodeValidationResponse response = matchService.validateInviteCode(request.getInviteCode());
    return ResponseEntity.ok(response);
  }

}
