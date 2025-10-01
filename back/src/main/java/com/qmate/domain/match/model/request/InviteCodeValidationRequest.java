package com.qmate.domain.match.model.request;

import com.qmate.common.constants.match.MatchConstants;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InviteCodeValidationRequest {

  @NotBlank(message = MatchConstants.INVITE_CODE_NOT_BLANK)
  @Size(min = 6, max = 6, message = MatchConstants.INVITE_CODE_SIZE)
  private String inviteCode;

}
