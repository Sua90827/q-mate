package com.qmate.api;

import com.qmate.domain.auth.JwtService;
import com.qmate.domain.auth.NaverOAuthService;
import com.qmate.domain.auth.NaverOAuthService.NaverTokenResponse;
import com.qmate.domain.auth.NaverOAuthService.NaverUserProfile;
import com.qmate.domain.auth.SocialAccountService;
import com.qmate.domain.auth.model.response.LoginResponse;
import com.qmate.domain.user.User;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class NaverAuthController {

  private final NaverOAuthService naverOAuthService;
  private final SocialAccountService socialAccountService;
  private final JwtService jwtService;

  @PostMapping("/auth/naver/exchange")
  public ResponseEntity<LoginResponse> exchange(@RequestBody NaverCodeExchangeRequest req) {
    // 1) 토큰 교환
    NaverTokenResponse tokenRes = naverOAuthService.exchange(req.code(), req.state(), req.redirectUri());

    // 2) 프로필 조회
    NaverUserProfile profile = naverOAuthService.fetchProfile(tokenRes.getAccess_token());

    // 3) 사용자 upsert
    String naverId  = profile.getId();               // 필수
    String email    = profile.getEmail();            // null 가능
    String nickname = (profile.getNickname() != null && !profile.getNickname().isBlank())
        ? profile.getNickname()
        : profile.getName();                         // 이름도 없으면 upsert에서 fallback
    LocalDate birth = null;
    // birthday: "MM-DD", birthyear: "YYYY" — 둘 다 있을 때만 LocalDate로 조합
    if (profile.getBirthyear() != null && profile.getBirthday() != null && profile.getBirthday().length() == 5) {
      try {
        String yyyy = profile.getBirthyear();
        String mm = profile.getBirthday().substring(0, 2);
        String dd = profile.getBirthday().substring(3, 5);
        birth = LocalDate.parse(yyyy + "-" + mm + "-" + dd);
      } catch (Exception ignored) {}
    }

    User user = socialAccountService.upsertNaverUser(naverId, email, nickname, birth);

    // 4) 앱 JWT 발급
    var pair = jwtService.issue(user.getId(), user.getRole().name(), user.getEmail());

    // 5) 응답
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
        .accessTokenExpiresIn(pair.getAccessTokenTtlSeconds())
        .refreshTokenExpiresIn(pair.getRefreshTokenTtlSeconds())
        .user(summary)
        .build();

    return ResponseEntity.ok(body);
  }

  public record NaverCodeExchangeRequest(
      String code,
      String state,
      String redirectUri
  ) {}
}