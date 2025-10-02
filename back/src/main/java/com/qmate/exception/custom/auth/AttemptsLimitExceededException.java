package com.qmate.exception.custom.auth;

import com.qmate.exception.BusinessGlobalException;
import com.qmate.exception.errorcode.EmailVerificationErrorCode;

public class AttemptsLimitExceededException extends BusinessGlobalException {
  public AttemptsLimitExceededException() {
    super(EmailVerificationErrorCode.attemptsExceeded());
  }
}
