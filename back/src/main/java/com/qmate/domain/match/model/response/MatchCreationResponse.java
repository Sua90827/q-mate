package com.qmate.domain.match.model.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MatchCreationResponse {

  private final String inviteCode;
  private final Long matchId;

}
