package com.qmate.api;

import com.qmate.common.constants.auth.AuthConstants;
import com.qmate.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@Tag(name = "Auth", description = "인증 관련 API")
public class AuthMeController {

  public record MeResponse(Long userId, String email, String role) {}

  @GetMapping("/me")//프로필만
  @Operation(
      summary = "프로필만 조회"
  )
  public MeResponse me(@AuthenticationPrincipal UserPrincipal p) {
    return new MeResponse(p.userId(), p.email(), p.role());
  }
}