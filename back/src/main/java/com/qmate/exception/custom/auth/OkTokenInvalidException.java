package com.qmate.exception.custom.auth;

import com.qmate.exception.BusinessGlobalException;
import com.qmate.exception.errorcode.EmailVerificationErrorCode;

public class OkTokenInvalidException extends BusinessGlobalException {
  public OkTokenInvalidException() {
    super(EmailVerificationErrorCode.okTokenInvalid());
  }
}