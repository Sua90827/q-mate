package com.qmate.common.constants.auth;

import com.qmate.common.constants.HttpStatusCode;
import com.qmate.exception.errorcode.EmailVerificationErrorCode;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EmailVerificationConstants {

  public static final String SEND_MD =
    "인증코드를 이메일로 전송합니다.\n\n"
      + "### 에러 응답\n\n"
      + "| HTTP | errorCode | message |\n"
      + "|-----:|-----|---------|\n"
      + "|" + HttpStatusCode.TOO_MANY_REQUESTS + " | " + EmailVerificationErrorCode.RESEND_COOLDOWN_CODE + " | "
      + EmailVerificationErrorCode.RESEND_COOLDOWN_MSG
      + "|\n";

  public static final String RESEND_MD =
    "인증코드를 재전송합니다.\n\n"
      + "### 에러 응답\n\n"
      + "| HTTP | errorCode | message |\n"
      + "|-----:|-----|---------|\n"
      + "|" + HttpStatusCode.TOO_MANY_REQUESTS + " | " + EmailVerificationErrorCode.RESEND_COOLDOWN_CODE + " | "
      + EmailVerificationErrorCode.RESEND_COOLDOWN_MSG
      + "|\n";

  public static final String VERIFY_MD =
    "인증코드를 검증하고 OK 토큰을 발급합니다.\n\n"
      + "### 에러 응답\n\n"
      + "| HTTP | errorCode | message |\n"
      + "|-----:|-----|---------|\n"
      + "|" + HttpStatusCode.BAD_REQUEST + " | " + EmailVerificationErrorCode.CODE_INVALID_OR_EXPIRED_CODE + " | "
      + EmailVerificationErrorCode.CODE_INVALID_OR_EXPIRED_MSG
      + "|\n"
      + "|" + HttpStatusCode.TOO_MANY_REQUESTS + " | " + EmailVerificationErrorCode.ATTEMPTS_EXCEEDED_CODE + " | "
      + EmailVerificationErrorCode.ATTEMPTS_EXCEEDED_MSG
      + "|\n";
}
