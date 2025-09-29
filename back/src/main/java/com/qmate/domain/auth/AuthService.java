package com.qmate.domain.auth;

import com.qmate.domain.user.User;
import com.qmate.domain.user.UserRepository;
import com.qmate.domain.auth.model.response.LoginResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class AuthService {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;


  public LoginResponse login(String email, String rawPassword) {
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

    if (!passwordEncoder.matches(rawPassword, user.getPasswordHash())) {
      throw new BadCredentialsException("Invalid email or password");
    }

    JwtService.TokenPair pair = jwtService.issue(user.getId(), user.getRole().name(), user.getEmail());

    LoginResponse.UserSummary summary = LoginResponse.UserSummary.builder()
        .userId(user.getId())
        .email(user.getEmail())
        .nickname(user.getNickname())
        .role(user.getRole().name())
        .currentMatchId(user.getCurrentMatchId())
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
}