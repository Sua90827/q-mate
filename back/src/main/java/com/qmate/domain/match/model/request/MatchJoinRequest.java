package com.qmate.domain.match.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MatchJoinRequest {

  @NotBlank(message = "초대 코드를 입력해주세요.")
  @Size(min = 6, max = 6, message = "초대 코드는 6자리 숫자입니다.")
  private String inviteCode;

}
