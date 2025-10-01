package com.qmate.exception.errorcode;

import com.qmate.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public class UserErrorCode extends ErrorCode {

  public static final String NICKNAME_TOO_LONG_CODE = "US_001";
  public static final String NICKNAME_TOO_LONG_MSG  = "닉네임은 50자 이내여야 합니다.";

  public static final String BIRTHDATE_IN_FUTURE_CODE = "US_002";
  public static final String BIRTHDATE_IN_FUTURE_MSG  = "생년월일은 미래 날짜일 수 없습니다.";

  public static final String EMAIL_ALREADY_IN_USE_CODE = "US_003";
  public static final String EMAIL_ALREADY_IN_USE_MSG  = "이미 사용 중인 이메일입니다.";

  public static ErrorCode nicknameTooLong() {
    return new UserErrorCode(HttpStatus.BAD_REQUEST, NICKNAME_TOO_LONG_CODE, NICKNAME_TOO_LONG_MSG);
  }

  public static ErrorCode birthDateInFuture() {
    return new UserErrorCode(HttpStatus.BAD_REQUEST, BIRTHDATE_IN_FUTURE_CODE, BIRTHDATE_IN_FUTURE_MSG);
  }

  public static ErrorCode emailAlreadyInUse() {
    return new UserErrorCode(HttpStatus.CONFLICT, EMAIL_ALREADY_IN_USE_CODE, EMAIL_ALREADY_IN_USE_MSG);
  }

  private UserErrorCode(HttpStatus status, String code, String message) {
    super(status, code, message);
  }
}