package com.qmate.exception.errorcode;

import com.qmate.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public class EmailVerificationErrorCode extends ErrorCode {

  public static final String RESEND_COOLDOWN_CODE = "EV_001";
  public static final String RESEND_COOLDOWN_MSG  = "인증코드 재전송 대기 시간입니다.";

  public static final String CODE_INVALID_OR_EXPIRED_CODE = "EV_002";
  public static final String CODE_INVALID_OR_EXPIRED_MSG  = "인증코드가 만료되었거나 유효하지 않습니다.";

  public static final String ATTEMPTS_EXCEEDED_CODE = "EV_003";
  public static final String ATTEMPTS_EXCEEDED_MSG  = "인증 시도 한도를 초과했습니다.";

  public static final String OK_TOKEN_INVALID_CODE = "EV_004";
  public static final String OK_TOKEN_INVALID_MSG  = "이메일 인증 확인 토큰이 유효하지 않거나 만료되었습니다.";

  public static ErrorCode resendCooldown() {
    return new EmailVerificationErrorCode(HttpStatus.TOO_MANY_REQUESTS, RESEND_COOLDOWN_CODE, RESEND_COOLDOWN_MSG);
  }

  public static ErrorCode codeInvalidOrExpired() {
    return new EmailVerificationErrorCode(HttpStatus.BAD_REQUEST, CODE_INVALID_OR_EXPIRED_CODE, CODE_INVALID_OR_EXPIRED_MSG);
  }

  public static ErrorCode attemptsExceeded() {
    return new EmailVerificationErrorCode(HttpStatus.TOO_MANY_REQUESTS, ATTEMPTS_EXCEEDED_CODE, ATTEMPTS_EXCEEDED_MSG);
  }

  public static ErrorCode okTokenInvalid() {
    return new EmailVerificationErrorCode(HttpStatus.BAD_REQUEST, OK_TOKEN_INVALID_CODE, OK_TOKEN_INVALID_MSG);
  }

  private EmailVerificationErrorCode(HttpStatus status, String code, String message) {
    super(status, code, message);
  }
}