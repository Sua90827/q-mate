package com.qmate.api;

import com.qmate.domain.auth.EmailVerificationService;
import com.qmate.domain.auth.model.request.EmailVerificationSendRequest;
import com.qmate.domain.user.UserService;
import com.qmate.domain.user.model.request.RegisterRequest;
import com.qmate.domain.user.model.response.RegisterResponse;
import jakarta.validation.Valid;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

  private final UserService userService;
  private final EmailVerificationService emailVerificationService;

  @PostMapping("/register")
  public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest req){
    //ok 토큰 소비
    if (!emailVerificationService.consumeOkToken(req.getEmailVerifiedToken(), "signup", req.getEmail())) {
      throw new IllegalStateException("이메일 인증이 확인되지 않음.");
    }
    //가입
    Long id = userService.register(req);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(new RegisterResponse(id.toString(), true));
  }
}
