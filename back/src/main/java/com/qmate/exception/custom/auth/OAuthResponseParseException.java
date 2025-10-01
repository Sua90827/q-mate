package com.qmate.exception.custom.auth;

import com.qmate.exception.BusinessGlobalException;
import com.qmate.exception.errorcode.AuthErrorCode;

public class OAuthResponseParseException extends BusinessGlobalException {
  public OAuthResponseParseException() {
    super(AuthErrorCode.oauthResponseParseFailed());
  }
}