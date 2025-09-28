package com.qmate.exception.custom.matchinstance;

import com.qmate.exception.BusinessGlobalException;
import com.qmate.exception.MatchErrorCode;

//초대코드 만료 또는 유효하지 않음 예외
public class InviteCodeExpiredException extends BusinessGlobalException {

  public InviteCodeExpiredException() {
    super(MatchErrorCode.inviteCodeExpired());
  }

}
