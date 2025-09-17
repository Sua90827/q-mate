package com.qmate.api;

import com.qmate.domain.auth.EmailVerificationService;
import com.qmate.domain.auth.model.request.EmailVerificationSendRequest;
import com.qmate.domain.auth.model.response.EmailConfirmResponse;
import com.qmate.domain.auth.model.response.EmailResentResponse;
import com.qmate.domain.auth.model.request.EmailVerificationConfirmRequest;
import com.qmate.domain.auth.model.response.EmailSentResponse;
import jakarta.validation.Valid;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth/email-verifications")
@RequiredArgsConstructor
public class EmailVerificationController {
  private final EmailVerificationService emailVerificationService;

  @PostMapping
  public ResponseEntity<EmailSentResponse> send(@Valid @RequestBody EmailVerificationSendRequest req) {
    emailVerificationService.sendCode(req.getEmail(), req.getPurpose());
    return ResponseEntity.ok(new EmailSentResponse(true));
  }

  @PostMapping("/resend")
  public ResponseEntity<?> resend(@Valid @RequestBody EmailVerificationSendRequest req) {
    try {
      emailVerificationService.sendCode(req.getEmail(), req.getPurpose());
      return ResponseEntity.ok(new EmailResentResponse(true));
    } catch (IllegalStateException e) { // RESEND_COOLDOWN
      return ResponseEntity.status(429).body(Map.of("error","RESEND_COOLDOWN"));
    }
  }

  @PostMapping("/verify")
  public ResponseEntity<?> verify(@Valid @RequestBody EmailVerificationConfirmRequest req) {
    try {
      String token = emailVerificationService.verifyAndIssueToken(req.getEmail(), req.getPurpose(), req.getCode());
      return ResponseEntity.ok(new EmailConfirmResponse(true, token));
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body(new EmailConfirmResponse(false, null));
    }
  }
}