package com.qmate.api;

import com.qmate.common.constants.auth.EmailVerificationConstants;
import com.qmate.common.constants.question.QuestionCategoryConstants;
import com.qmate.domain.auth.EmailVerificationService;
import com.qmate.domain.auth.model.request.EmailVerificationSendRequest;
import com.qmate.domain.auth.model.response.EmailConfirmResponse;
import com.qmate.domain.auth.model.response.EmailResentResponse;
import com.qmate.domain.auth.model.request.EmailVerificationConfirmRequest;
import com.qmate.domain.auth.model.response.EmailSentResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth/email-verifications")
@RequiredArgsConstructor
@Tag(name = "Email Verification", description = "이메일 인증 관련 API")
public class EmailVerificationController {
  private final EmailVerificationService emailVerificationService;

  @PostMapping
  @Operation(
      summary = "이메일 인증 전송",
      description = EmailVerificationConstants.SEND_MD
  )
  public ResponseEntity<EmailSentResponse> send(@Valid @RequestBody EmailVerificationSendRequest req) {
    emailVerificationService.sendCode(req.getEmail(), req.getPurpose());
    return ResponseEntity.ok(new EmailSentResponse(true));
  }

  @PostMapping("/resend")
  @Operation(
      summary = "이메일 인증 재전송",
      description = EmailVerificationConstants.RESEND_MD
  )
  public ResponseEntity<EmailResentResponse> resend(@Valid @RequestBody EmailVerificationSendRequest req) {
    try {
      emailVerificationService.sendCode(req.getEmail(), req.getPurpose());
      return ResponseEntity.ok(new EmailResentResponse(true));
    } catch (IllegalStateException e) { // RESEND_COOLDOWN
      return ResponseEntity.status(429).body(new EmailResentResponse(false));
    }
  }

  @PostMapping("/verify")
  @Operation(
      summary = "이메일 인증코드 검증(OK 토큰 발급)",
      description = EmailVerificationConstants.VERIFY_MD
  )
  public ResponseEntity<EmailConfirmResponse> verify(@Valid @RequestBody EmailVerificationConfirmRequest req) {
    try {
      String token = emailVerificationService.verifyAndIssueToken(req.getEmail(), req.getPurpose(), req.getCode());
      return ResponseEntity.ok(new EmailConfirmResponse(true, token));
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body(new EmailConfirmResponse(false, null));
    }
  }
}