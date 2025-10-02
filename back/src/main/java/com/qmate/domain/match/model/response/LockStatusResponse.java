package com.qmate.domain.match.model.response;

import lombok.Getter;

@Getter
public class LockStatusResponse {

  private final boolean isLocked;
  private final Long remainingSeconds;
  // 남은 잠금 시간(초 단위) front에서 핸들링해서 사용가능 하게끔 Long타입 반환.

  public LockStatusResponse(boolean isLocked, Long remainingSeconds) {
    this.isLocked = isLocked;
    this.remainingSeconds = remainingSeconds;
  }

}
