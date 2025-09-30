package com.qmate.domain.match.model.response;

import lombok.Getter;

@Getter
public class LockStatusResponse {

  private final boolean isLocked;
  private final Long remainingSeconds;// 남은 잠금 시간(초 단위)

  public LockStatusResponse(boolean isLocked, Long remainingSeconds) {
    this.isLocked = isLocked;
    this.remainingSeconds = remainingSeconds;
  }

}
