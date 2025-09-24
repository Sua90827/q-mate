package com.qmate.domain.match.model.response;

import com.qmate.domain.user.User;
import java.time.LocalDate;
import lombok.Getter;

@Getter
public class MatchMemberDetailResponse {

  private final Long userId;
  private final String nickname;
  private final LocalDate birthDate;

  public MatchMemberDetailResponse(User user) {
    this.userId = user.getId();
    this.nickname = user.getNickname();
    this.birthDate = user.getBirthDate();
  }

}
