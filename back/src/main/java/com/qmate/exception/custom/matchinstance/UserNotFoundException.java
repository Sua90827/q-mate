package com.qmate.exception.custom.matchinstance;

import com.qmate.exception.BusinessGlobalException;
import com.qmate.exception.UserErrorCode;

//사용자를 찾을 수 없음 예외
public class UserNotFoundException extends BusinessGlobalException {

  public UserNotFoundException() {
    super(UserErrorCode.userNotFound());
  }

}
