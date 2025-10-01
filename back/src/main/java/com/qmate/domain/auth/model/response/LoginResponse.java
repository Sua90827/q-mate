package com.qmate.domain.auth.model.response;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class LoginResponse {
  private final String accessToken;
  private final String refreshToken;
  private final String tokenType;//"Bearer"
  private final long accessTokenExpiresIn;//초단위
  private final long refreshTokenExpiresIn;//초단위
  private final UserSummary user;

  @Getter
  @Builder
  @AllArgsConstructor
  public static class UserSummary {
    private final Long userId;
    private final String email;
    private final String nickname;
    private final LocalDate birthDate;
    private final String role;//"USER", "ADMIN"
    private final Long currentMatchId; //null 가능
    private final boolean pushEnabled;
  }
}