package com.qmate.domain.match.model.response;

import com.qmate.domain.match.Match;
import java.util.List;
import lombok.Getter;

@Getter
public class MatchMembersResponse {

  private final Long matchId;
  private final List<MatchMemberDetailResponse> members;

  public MatchMembersResponse(Match match, Long requesterId) {
    this.matchId = match.getId();
    this.members = match.getMembers().stream()
        .map(matchMember -> new MatchMemberDetailResponse(matchMember.getUser(), requesterId))
        .toList();
  }

}
