package com.qmate.domain.match.model.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MatchJoinResponse {

  private final Long matchId;
  private final String message;
  private String partnerNickname;

}
