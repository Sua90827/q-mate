package com.qmate.exception.custom.auth;

import com.qmate.exception.BusinessGlobalException;
import com.qmate.exception.errorcode.AuthErrorCode;

public class OAuthResponseMissingIdException extends BusinessGlobalException {
  public OAuthResponseMissingIdException() {
    super(AuthErrorCode.oauthResponseMissingId());
  }
}