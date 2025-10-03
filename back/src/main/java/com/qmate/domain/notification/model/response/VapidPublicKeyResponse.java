package com.qmate.domain.notification.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VapidPublicKeyResponse {
  private String vapidPublicKey;
}
