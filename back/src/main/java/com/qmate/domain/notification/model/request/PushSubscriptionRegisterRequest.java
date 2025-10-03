package com.qmate.domain.notification.model.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class PushSubscriptionRegisterRequest {

  @NotBlank
  private String endpoint;

  @Valid
  @JsonProperty("keys")
  private Keys keys;

  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class Keys {

    @NotBlank
    private String p256dh;

    @NotBlank
    private String auth;
  }

  // 편의 접근자: 서비스에서 바로 쓰기 좋게
  public String getKeyP256dh() {
    return keys != null ? keys.p256dh : null;
  }

  public String getKeyAuth() {
    return keys != null ? keys.auth : null;
  }
}
