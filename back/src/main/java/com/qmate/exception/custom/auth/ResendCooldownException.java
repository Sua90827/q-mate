package com.qmate.exception.custom.auth;

import com.qmate.exception.BusinessGlobalException;
import com.qmate.exception.errorcode.EmailVerificationErrorCode;

public class ResendCooldownException extends BusinessGlobalException {
  public ResendCooldownException() {
    super(EmailVerificationErrorCode.resendCooldown());
  }
}
