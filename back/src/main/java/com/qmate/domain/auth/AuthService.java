package com.qmate.domain.auth;

import com.qmate.domain.user.User;
import com.qmate.domain.user.UserRepository;
import com.qmate.domain.auth.model.response.LoginResponse;
import com.qmate.exception.custom.auth.InvalidCredentialsException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class AuthService {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final SecurityContextLogoutHandler delegate = new SecurityContextLogoutHandler();


  public LoginResponse login(String email, String rawPassword) {
    User user = userRepository.findByEmail(email)
        .orElseThrow(InvalidCredentialsException::new);

    if (!passwordEncoder.matches(rawPassword, user.getPasswordHash())) {
      throw new InvalidCredentialsException();
    }

    JwtService.TokenPair pair = jwtService.issue(user.getId(), user.getRole().name(), user.getEmail());

    LoginResponse.UserSummary summary = LoginResponse.UserSummary.builder()
        .userId(user.getId())
        .email(user.getEmail())
        .nickname(user.getNickname())
        .role(user.getRole().name())
        .currentMatchId(user.getCurrentMatchId())
        .pushEnabled(user.isPushEnabled())
        .build();

    return LoginResponse.builder()
        .accessToken(pair.getAccessToken())
        .refreshToken(pair.getRefreshToken())
        .tokenType("Bearer")
        .accessTokenExpiresIn(pair.getAccessTokenTtlSeconds())
        .refreshTokenExpiresIn(pair.getRefreshTokenTtlSeconds())
        .user(summary)
        .build();
  }

  public void logout(HttpServletRequest req, HttpServletResponse res, Authentication auth) {
    delegate.logout(req, res, auth); //세션, 컨텍스트 정리
    expireCookie(res, "ACCESS_TOKEN");// JWT 쿠키 만료
  }

  private void expireCookie(HttpServletResponse res, String name) {
    ResponseCookie expired = ResponseCookie.from(name, "")
        .httpOnly(true)
        .secure(true)
        .sameSite("None")
        .path("/")
        .maxAge(0)
        .build();
    res.addHeader("Set-Cookie", expired.toString());
  }
}