package com.qmate.api;

import com.qmate.common.constants.auth.AuthConstants;
import com.qmate.common.constants.question.QuestionCategoryConstants;
import com.qmate.domain.auth.AuthService;
import com.qmate.domain.auth.EmailVerificationService;
import com.qmate.domain.auth.model.request.LoginRequest;
import com.qmate.domain.auth.model.response.LoginResponse;
import com.qmate.domain.user.UserService;
import com.qmate.domain.user.model.request.RegisterRequest;
import com.qmate.domain.user.model.response.RegisterResponse;
import com.qmate.exception.custom.auth.OkTokenInvalidException;
import com.qmate.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "인증 관련 API")
public class AuthController {

  private final UserService userService;
  private final EmailVerificationService emailVerificationService;
  private final AuthService authService;

  @PostMapping("/register")
  @Operation(
      summary = "자체 회원가입",
      description = AuthConstants.CREATE_MD
  )
  public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest req){
    //ok 토큰 소비
    if(!emailVerificationService.consumeOkToken(req.getEmailVerifiedToken(), "signup", req.getEmail())){
      throw new OkTokenInvalidException();
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

  //@PreAuthorize("hasRole('ADMIN')")
  @GetMapping("/session")
  public Map<String, Object> session(@AuthenticationPrincipal UserPrincipal me){
    var map = new java.util.LinkedHashMap<String, Object>();
    map.put("userId", me.userId());
    map.put("role",   me.role());
    if (me.email() != null) map.put("email", me.email());
    return map;
  }
}
