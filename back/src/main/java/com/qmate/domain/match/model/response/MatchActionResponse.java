package com.qmate.domain.match.model.response;

import lombok.Getter;

@Getter
public class MatchActionResponse {

  private final String message;

  public MatchActionResponse(String message){
    this.message = message;
  }

}
