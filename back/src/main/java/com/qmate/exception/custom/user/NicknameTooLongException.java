package com.qmate.exception.custom.user;

import com.qmate.exception.BusinessGlobalException;
import com.qmate.exception.errorcode.UserErrorCode;

public class NicknameTooLongException extends BusinessGlobalException {
  public NicknameTooLongException() {
    super(UserErrorCode.nicknameTooLong());
  }
}