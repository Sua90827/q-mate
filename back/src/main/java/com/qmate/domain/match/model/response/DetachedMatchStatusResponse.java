package com.qmate.domain.match.model.response;

import lombok.Getter;

@Getter
public class DetachedMatchStatusResponse {
  private final boolean hasDetachedMatch;
  private final Long matchId; // 복구 가능한 매칭이 있을 경우 그 ID

  public DetachedMatchStatusResponse(boolean hasDetachedMatch, Long matchId){
    this.hasDetachedMatch = hasDetachedMatch;
    this.matchId = matchId;
  }

}
