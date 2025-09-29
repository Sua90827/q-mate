package com.qmate.security.oauth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qmate.domain.auth.JwtService;
import com.qmate.domain.auth.SocialAccountService;
import com.qmate.domain.auth.model.response.LoginResponse;
import com.qmate.domain.user.UserSocialAccount.SocialProvider;
import com.qmate.security.UserPrincipal;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

  private final JwtService jwtService;
  private final SocialAccountService socialAccountService;
  private final ObjectMapper om = new ObjectMapper();

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) throws IOException {

    Long userId;
    String email;
    String role;

    Object p = authentication.getPrincipal();

    if (p instanceof CustomOAuth2User ou) {
      // 네이버 등 OAuth2 (기존 플로우)
      userId = ou.getUserId();
      email  = ou.getEmail();
      role   = ou.getRole();

    } else if (p instanceof OidcUser oidc) {
      // 구글 OIDC (openid 스코프)
      String sub  = oidc.getSubject();
      String mail = oidc.getEmail(); // scope에 email 포함되어 있으면 옴
      String name = (String) oidc.getClaims().getOrDefault("name", mail);

      var user = socialAccountService.upsertSocialUser(
          SocialProvider.GOOGLE, sub, mail, name
      );

      userId = user.getId();
      email  = user.getEmail();
      role   = user.getRole().name();

    } else {
      throw new IllegalStateException("Unsupported principal type: " + p.getClass());
    }
    // 1) 소셜도 UserPrincipal로 통일
    var principal = new UserPrincipal(userId, email, role);
    var auth = new UsernamePasswordAuthenticationToken(
        principal, null,
        java.util.List.of(new SimpleGrantedAuthority("ROLE_" + role))
    );
    SecurityContextHolder.getContext().setAuthentication(auth);

    // 2) JWT 발급 (자체 로그인과 동일)
    var pair = jwtService.issue(principal.userId(), principal.role(), principal.email());

    var body = LoginResponse.builder()
        .accessToken(pair.getAccessToken())
        .refreshToken(pair.getRefreshToken())
        .tokenType("Bearer")
        .accessTokenExpiresIn(pair.getAccessTokenTtlSeconds())
        .refreshTokenExpiresIn(pair.getRefreshTokenTtlSeconds())
        .build();

    // 3) JSON 응답 (원하면 여기서 sendRedirect(...) 방식으로 교체 가능)
    response.setStatus(HttpServletResponse.SC_OK);
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    om.writeValue(response.getOutputStream(), body);
  }
}