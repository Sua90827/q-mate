package com.qmate.common.constants.auth;

import com.qmate.common.constants.HttpStatusCode;
import com.qmate.exception.errorcode.AuthErrorCode;
import com.qmate.exception.errorcode.EmailVerificationErrorCode;
import com.qmate.exception.errorcode.UserErrorCode;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AuthConstants {

  public static final String REGISTER_MD =
    "자체 회원 가입을 진행합니다.\n\n"
      + "### 에러 응답\n\n"
      + "| HTTP | errorCode | message |\n"
      + "|-----:|-----------|---------|\n"
      + "| " + HttpStatusCode.BAD_REQUEST + " | " + EmailVerificationErrorCode.OK_TOKEN_INVALID_CODE + " | "
      + EmailVerificationErrorCode.OK_TOKEN_INVALID_MSG + " |\n"
      + "| " + HttpStatusCode.CONFLICT + " | " + UserErrorCode.EMAIL_ALREADY_IN_USE_CODE + " | "
      + UserErrorCode.EMAIL_ALREADY_IN_USE_MSG
      + "|\n";

  public static final String LOGIN_MD =
    "자체 로그인을 진행합니다.\n\n"
      + "### 에러 응답\n\n"
      + "| HTTP | errorCode | message |\n"
      + "|-----:|-----------|---------|\n"
      + "| " + HttpStatusCode.UNAUTHORIZED + " | " + AuthErrorCode.INVALID_CREDENTIALS_CODE + " | "
      + AuthErrorCode.INVALID_CREDENTIALS_MESSAGE + " |\n";
}
