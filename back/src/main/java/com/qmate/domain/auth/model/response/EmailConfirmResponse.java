package com.qmate.domain.auth.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EmailConfirmResponse {
  private final boolean verified;

  @JsonProperty("email_verified_token")
  private final String emailVerifiedToken;// 검증 성공 시 짧은 TTL로 발급
}
