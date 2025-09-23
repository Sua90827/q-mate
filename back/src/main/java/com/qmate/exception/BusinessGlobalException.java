package com.qmate.exception;

import lombok.Getter;

@Getter
public class BusinessGlobalException extends RuntimeException {

  private final ErrorCode errorCode;

  public BusinessGlobalException(ErrorCode errorCode) {
    super(errorCode.getMessage());
    this.errorCode = errorCode;
  }


}
