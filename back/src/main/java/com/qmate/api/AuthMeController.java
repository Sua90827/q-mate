package com.qmate.api;

import com.qmate.security.UserPrincipal;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class AuthMeController {

  public record MeResponse(Long userId, String email, String role) {}

  @GetMapping("/me")//프로필만
  public MeResponse me(@AuthenticationPrincipal UserPrincipal p) {
    return new MeResponse(p.userId(), p.email(), p.role());
  }
}