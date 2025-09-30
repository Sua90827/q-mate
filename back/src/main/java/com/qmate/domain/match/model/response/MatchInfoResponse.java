package com.qmate.domain.match.model.response;

import com.qmate.domain.match.Match;
import com.qmate.domain.match.MatchSetting;
import com.qmate.domain.match.MatchStatus;
import com.qmate.domain.match.RelationType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.Getter;

@Getter
public class MatchInfoResponse {

  private final Long matchId;
  private final RelationType relationType;
  private final LocalDateTime startDate;
  private final int dailyQuestionHour;
  private final MatchStatus status;
  private final List<MemberInfoResponse> users;


  // Match 엔티티를 통째로 받아서 이 DTO를 완성하는 생성자
  public MatchInfoResponse(Match match, Long requesterId) {
    this.matchId = match.getId();
    this.relationType = match.getRelationType();
    this.startDate = match.getStartDate();
    // MatchSetting이 존재하면 그 값을, 없으면(null) 기본값 12를 사용합니다.
    this.dailyQuestionHour = Optional.ofNullable(match.getMatchSetting())
        .map(MatchSetting::getDailyQuestionHour)
        .orElse(12);
    this.status = match.getStatus();

    // Match 엔티티가 가진 MatchMember 리스트를 순회하며 MemberInfo DTO 리스트로 변환
    this.users = match.getMembers().stream()
        .map(matchMember -> new MemberInfoResponse(matchMember, requesterId))
        .toList();
  }
}
