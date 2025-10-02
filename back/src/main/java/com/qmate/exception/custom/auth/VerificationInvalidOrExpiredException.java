package com.qmate.exception.custom.auth;

import com.qmate.exception.BusinessGlobalException;
import com.qmate.exception.errorcode.EmailVerificationErrorCode;

public class VerificationInvalidOrExpiredException extends BusinessGlobalException {
  public VerificationInvalidOrExpiredException() {
    super(EmailVerificationErrorCode.codeInvalidOrExpired());
  }
}