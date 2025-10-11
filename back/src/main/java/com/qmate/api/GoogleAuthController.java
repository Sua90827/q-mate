package com.qmate.api;

import com.qmate.domain.auth.GoogleOAuthService;
import com.qmate.domain.auth.GoogleOAuthService.GoogleTokenResponse;
import com.qmate.domain.auth.JwtService;
import com.qmate.domain.auth.SocialAccountService;
import com.qmate.domain.auth.model.response.LoginResponse;
import com.qmate.domain.user.User;
import com.qmate.domain.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@RestController
@RequiredArgsConstructor
public class GoogleAuthController {

  private final GoogleOAuthService googleOAuthService;
  private final SocialAccountService socialAccountService;
  private final JwtService jwtService;

  @PostMapping("/auth/google/exchange")
  public ResponseEntity<LoginResponse> exchange(@RequestBody GoogleCodeExchangeRequest req) {
    // 1) 구글 토큰 교환
    GoogleTokenResponse tokenRes = googleOAuthService.exchange(req.code(), req.redirectUri());

    // 2) id_token 검증
    var payload = googleOAuthService.verifyIdToken(tokenRes.getId_token());

    // 3) 사용자 upsert
    String sub   = payload.getSubject();
    String email = (String) payload.get("email");
    String name  = (String) payload.get("name");
    User user = socialAccountService.upsertGoogleUser(sub, email, name);

    // 4) 애플리케이션 토큰 발급
    var pair = jwtService.issue(user.getId(), user.getRole().name(), user.getEmail());

    // 5) LoginResponse 조립
    LoginResponse.UserSummary summary = LoginResponse.UserSummary.builder()
        .userId(user.getId())
        .email(user.getEmail())
        .nickname(user.getNickname())
        .birthDate(user.getBirthDate())
        .role(user.getRole().name())
        .currentMatchId(user.getCurrentMatchId())
        .pushEnabled(user.isPushEnabled())
        .build();

    LoginResponse body = LoginResponse.builder()
        .accessToken(pair.getAccessToken())
        .refreshToken(pair.getRefreshToken())
        .tokenType("Bearer")
        .accessTokenExpiresIn(pair.getAccessTokenTtlSeconds())   // 초 단위
        .refreshTokenExpiresIn(pair.getRefreshTokenTtlSeconds()) // 초 단위
        .user(
            summary
        )
        .build();

    return ResponseEntity.ok(body);
  }

  // === 요청 DTO ===
  public record GoogleCodeExchangeRequest(
      String code,
      String redirectUri,
      String state,
      String nonce
  ) {}
}
