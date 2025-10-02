package com.qmate.common.constants.auth;

import com.qmate.common.constants.HttpStatusCode;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AuthConstants {
  public static final String CREATE_MD =
      "특정 매치에 대한 커스텀 질문을 생성합니다.\n\n"
          + "### 에러 응답\n\n"
          + "| HTTP | errorCode | message |\n"
          + "|-----:|-----------|---------|\n"
          + "| " + HttpStatusCode.NOT_FOUND + " | " + "MATCH_002" + " | "
          + "해당 매칭을 찾을 수 없습니다." + " |\n";

}
