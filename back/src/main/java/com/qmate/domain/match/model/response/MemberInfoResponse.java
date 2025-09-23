package com.qmate.domain.match.model.response;

import com.qmate.domain.user.User;
import lombok.Getter;

@Getter
public class MemberInfoResponse {

  private final Long userId;
  private final String nickname;

  // User 엔티티를 받아서 필요한 정보만 뽑아내는 생성자
  public MemberInfoResponse(User user){
    this.userId = user.getId();
    this.nickname = user.getNickname();
  }

}
