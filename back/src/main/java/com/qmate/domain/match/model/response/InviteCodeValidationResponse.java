package com.qmate.domain.match.model.response;

import lombok.Getter;

@Getter
public class InviteCodeValidationResponse {

  private final boolean isValid;
  private final String partnerNickname;

  public InviteCodeValidationResponse(boolean isValid, String partnerNickname){
    this.isValid = isValid;
    this.partnerNickname = partnerNickname;
  }

}
