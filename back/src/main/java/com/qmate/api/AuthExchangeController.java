package com.qmate.api;

import com.qmate.domain.auth.JwtService;
import com.qmate.domain.auth.model.response.LoginResponse;
import com.qmate.domain.user.UserRepository;
import com.qmate.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthExchangeController {

  private final JwtService jwtService;
  private final UserRepository userRepository;

  @PostMapping("/exchange")
  public ResponseEntity<LoginResponse> exchange(
      @CookieValue(name = "ACCESS_TOKEN", required = false) String accessCookie,
      @AuthenticationPrincipal UserPrincipal principal
  ) {
    if (accessCookie == null || !jwtService.isValid(accessCookie)) {
      return ResponseEntity.status(401).build(); // 액세스 토큰만 검사, 실패면 끝
    }

    // principal이 null이면 jwt 필터가 컨텍스트를 못 세팅한 케이스 → 401
    if (principal == null)
      return ResponseEntity.status(401).build();

    var user = userRepository.findById(principal.userId()).orElseThrow();

    long accessTtl = jwtService.getRemainingTtlSeconds(accessCookie);

    var body = LoginResponse.builder()
        .accessToken(accessCookie)
        .refreshToken(null)
        .tokenType("Bearer")
        .accessTokenExpiresIn(accessTtl)
        .refreshTokenExpiresIn(0)
        .user(LoginResponse.UserSummary.builder()
            .userId(user.getId())
            .email(user.getEmail())
            .nickname(user.getNickname())
            .birthDate(user.getBirthDate())
            .role(user.getRole().name())
            .currentMatchId(user.getCurrentMatchId())
            .pushEnabled(user.isPushEnabled())
            .build())
        .build();

    return ResponseEntity.ok(body);
  }
}