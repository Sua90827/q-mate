package com.qmate.domain.match.model.response;

import com.qmate.domain.user.User;
import java.time.LocalDate;
import lombok.Getter;

@Getter
public class MatchMemberDetailResponse {

  private final Long userId;
  private final String nickname;
  private final LocalDate birthDate;
  private final boolean isMe;

  public MatchMemberDetailResponse(User user, Long requesterId) {
    this.userId = user.getId();
    this.nickname = user.getNickname();
    this.birthDate = user.getBirthDate();
    this.isMe = user.getId().equals(requesterId);
  }

}
