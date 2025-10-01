package com.qmate.exception.custom.auth;

import com.qmate.exception.BusinessGlobalException;
import com.qmate.exception.errorcode.AuthErrorCode;

public class UnsupportedPrincipalTypeException extends BusinessGlobalException {
  public UnsupportedPrincipalTypeException() {
    super(AuthErrorCode.unsupportedPrincipalType());
  }
}