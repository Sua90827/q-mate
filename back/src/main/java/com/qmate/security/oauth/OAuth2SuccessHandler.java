package com.qmate.security.oauth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qmate.domain.auth.JwtService;
import com.qmate.domain.auth.SocialAccountService;
import com.qmate.domain.auth.model.response.LoginResponse;
import com.qmate.domain.user.User;
import com.qmate.domain.user.UserRepository;
import com.qmate.domain.user.UserSocialAccount.SocialProvider;
import com.qmate.exception.custom.auth.UnsupportedPrincipalTypeException;
import com.qmate.security.UserPrincipal;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

  private final JwtService jwtService;
  private final SocialAccountService socialAccountService;
  private final ObjectMapper om;
  private final UserRepository userRepository;

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) throws IOException {

    Object p = authentication.getPrincipal();
    User user;

    if (p instanceof CustomOAuth2User ou) {//네이버
      user = userRepository.findById(ou.getUserId()).orElseThrow();
    } else if (p instanceof OidcUser oidc) {//구글
      String sub  = oidc.getSubject();
      String mail = oidc.getEmail();
      String name = (String) oidc.getClaims().getOrDefault("name", mail);
      user = socialAccountService.upsertSocialUser(SocialProvider.GOOGLE, sub, mail, name, null);

      String birthdate = (String) oidc.getClaims().get("birthdate");
      log.debug("GOOGLE birthdate claim = {}", birthdate);
    } else {
      throw new UnsupportedPrincipalTypeException();
    }

    var principal = new UserPrincipal(user.getId(), user.getEmail(), user.getRole().name());

    var auth = new UsernamePasswordAuthenticationToken(
        principal, null,
        java.util.List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
    );
    SecurityContextHolder.getContext().setAuthentication(auth);

    var pair = jwtService.issue(user.getId(), user.getRole().name(), user.getEmail());

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

    response.setStatus(HttpServletResponse.SC_OK);
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.getWriter().write(om.writeValueAsString(body));
    response.getWriter().flush();
  }
}