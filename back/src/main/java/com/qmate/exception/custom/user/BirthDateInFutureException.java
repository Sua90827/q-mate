package com.qmate.exception.custom.user;

import com.qmate.exception.BusinessGlobalException;
import com.qmate.exception.errorcode.UserErrorCode;

public class BirthDateInFutureException extends BusinessGlobalException {
  public BirthDateInFutureException() {
    super(UserErrorCode.birthDateInFuture());
  }
}