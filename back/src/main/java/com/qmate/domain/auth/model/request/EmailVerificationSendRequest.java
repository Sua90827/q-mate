package com.qmate.domain.auth.model.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EmailVerificationSendRequest {
  @Email @NotBlank
  private String email;

  @NotBlank
  private String purpose;
}
