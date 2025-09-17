package com.qmate.domain.user.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RegisterResponse {
  @JsonProperty("user_id")
  private final String userId;
  private final boolean registered;
}
