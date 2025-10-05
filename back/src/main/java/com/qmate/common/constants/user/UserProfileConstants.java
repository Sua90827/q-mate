package com.qmate.common.constants.user;

import com.qmate.common.constants.HttpStatusCode;
import com.qmate.exception.errorcode.UserErrorCode;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserProfileConstants {

  public static final String PROFILE_MD =
    "사용자의 nickname, birthdate를 업데이트합니다.\n\n"
      + "### 에러 응답\n\n"
      + "| HTTP | errorCode | message |\n"
      + "|-----:|-----------|---------|\n"
      + "| " + HttpStatusCode.BAD_REQUEST + " | " + UserErrorCode.NICKNAME_TOO_LONG_CODE + " | "
      + UserErrorCode.NICKNAME_TOO_LONG_MSG + " |\n"
      + "| " + HttpStatusCode.BAD_REQUEST + " | " + UserErrorCode.BIRTHDATE_IN_FUTURE_CODE + " | "
      + UserErrorCode.BIRTHDATE_IN_FUTURE_MSG
      + "|\n";

  public static final String NICKNAME_MD =
    "사용자의 nickname을 업데이트합니다.\n\n"
      + "### 에러 응답\n\n"
      + "| HTTP | errorCode | message |\n"
      + "|-----:|-----------|---------|\n"
      + "| " + HttpStatusCode.BAD_REQUEST + " | " + UserErrorCode.NICKNAME_TOO_LONG_CODE + " | "
      + UserErrorCode.NICKNAME_TOO_LONG_MSG + " |\n"
//          + "| " + HttpStatusCode.BAD_REQUEST + " | " + UserErrorCode.BIRTHDATE_IN_FUTURE_CODE + " | "
//          + UserErrorCode.BIRTHDATE_IN_FUTURE_MSG + "|\n"
      + "| " + HttpStatusCode.NOT_FOUND + " | " + "USER_001" + " | "
      + com.qmate.exception.UserErrorCode.USER_NOT_FOUND_MESSAGE
      + "|\n";
}
