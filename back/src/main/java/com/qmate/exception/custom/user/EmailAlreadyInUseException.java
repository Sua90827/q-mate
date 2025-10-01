package com.qmate.exception.custom.user;

import com.qmate.exception.BusinessGlobalException;
import com.qmate.exception.errorcode.UserErrorCode;

public class EmailAlreadyInUseException extends BusinessGlobalException {
  public EmailAlreadyInUseException() {
    super(UserErrorCode.emailAlreadyInUse());
  }
}