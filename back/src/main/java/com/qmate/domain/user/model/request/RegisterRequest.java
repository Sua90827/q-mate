package com.qmate.domain.user.model.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RegisterRequest {
  @Email @NotBlank
  private String email;

  @NotBlank @Size(min = 8, max = 64)
  private String password;

  @NotBlank @Size(max = 50)
  private String nickname;

  @NotNull
  private LocalDate birthDate;

  @NotBlank
  private String emailVerifiedToken; //verify 성공 후 받은 토큰
}
