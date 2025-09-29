package com.qmate.domain.match.model.response;

import com.qmate.domain.match.MatchMember;
import com.qmate.domain.user.User;
import lombok.Getter;

@Getter
public class MemberInfoResponse {

  private final Long userId;
  private final String nickname;
  private final boolean isMe; //본인 여부 필드
  private final boolean isAgreed;

  // User 엔티티를 받아서 필요한 정보만 뽑아내는 생성자
  public MemberInfoResponse(MatchMember member, Long requesterId) {
    this.userId = member.getUser().getId();
    this.nickname = member.getUser().getNickname();
    this.isMe = member.getUser().getId().equals(requesterId);
    this.isAgreed = member.isAgreed();
  }

}
