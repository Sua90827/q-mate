package com.qmate.domain.auth.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EmailResentResponse {
  private final boolean resent;
}
