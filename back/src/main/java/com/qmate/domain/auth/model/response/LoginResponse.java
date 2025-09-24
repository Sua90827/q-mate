package com.qmate.domain.auth.model.response;

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
}