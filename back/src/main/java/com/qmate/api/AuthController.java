package com.qmate.api;

import com.qmate.domain.auth.AuthService;
import com.qmate.domain.auth.EmailVerificationService;
import com.qmate.domain.auth.model.request.LoginRequest;
import com.qmate.domain.auth.model.response.LoginResponse;
import com.qmate.domain.user.UserService;
import com.qmate.domain.user.model.request.RegisterRequest;
import com.qmate.domain.user.model.response.RegisterResponse;
import com.qmate.security.context.UserContext;
import jakarta.validation.Valid;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
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
  private final AuthService authService;

  @PostMapping("/register")
  public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest req){
    //ok 토큰 소비
    if(!emailVerificationService.consumeOkToken(req.getEmailVerifiedToken(), "signup", req.getEmail())){
      throw new IllegalStateException("이메일 인증이 확인되지 않음.");
    }
    //가입
    Long id = userService.register(req);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(new RegisterResponse(id.toString(), true));
  }

  @PostMapping("/login")
  public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request){
    LoginResponse tokens = authService.login(request.getEmail(), request.getPassword());
    return ResponseEntity.ok(tokens);
  }

  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping("/session")
  public Map<String, String> session(){
    return Map.of(
        "userId", UserContext.getUserId(),
        "role",   UserContext.getUserRole(),
        "email", UserContext.getUserEmail()
    );
  }
}
