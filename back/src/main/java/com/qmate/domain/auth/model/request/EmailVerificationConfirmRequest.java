package com.qmate.domain.auth.model.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EmailVerificationConfirmRequest {
  @Email @NotBlank
  private String email;

  @NotBlank @Size(min = 6, max = 6)
  private String code;

  @NotBlank
  private String purpose;
}
