package com.qmate.exception.custom.matchinstance;

import com.qmate.exception.BusinessGlobalException;
import com.qmate.exception.MatchErrorCode;

//초대코드 입력횟수 초과 예외
public class InviteAttemptLockedException extends BusinessGlobalException {
  public InviteAttemptLockedException(){
    super(MatchErrorCode.inviteAttemptLocked());
  }



}
