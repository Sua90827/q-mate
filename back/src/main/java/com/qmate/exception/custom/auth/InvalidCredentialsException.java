package com.qmate.exception.custom.auth;

import com.qmate.exception.BusinessGlobalException;
import com.qmate.exception.errorcode.AuthErrorCode;

public class InvalidCredentialsException extends BusinessGlobalException {

  public InvalidCredentialsException() {
    super(AuthErrorCode.invalidCredential());
  }
}
