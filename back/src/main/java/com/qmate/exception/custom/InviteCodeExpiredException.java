package com.qmate.exception.custom;

import com.qmate.exception.BusinessGlobalException;
import com.qmate.exception.ErrorCode;

//초대코드 만료 또는 유효하지 않음 예외
public class InviteCodeExpiredException extends BusinessGlobalException {
  public InviteCodeExpiredException(){
    super(ErrorCode.INVITE_CODE_EXPIRED);
  }

}
