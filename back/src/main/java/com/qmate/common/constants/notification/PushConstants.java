package com.qmate.common.constants.notification;

import com.qmate.common.constants.HttpStatusCode;
import com.qmate.exception.CommonErrorCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class PushConstants {

  // api docs
  public static final String UPDATE_PUSH_SETTING_MD =
  "사용자의 알림 사용 여부를 변경합니다.\n\n"
      + "### 에러 응답\n\n"
      + "| HTTP | errorCode | message |\n"
      + "|-----:|-----------|---------|\n"
      + "| " + HttpStatusCode.BAD_REQUEST + " | " + CommonErrorCode.INVALID_INPUT_ERROR_CODE + " | "
      + CommonErrorCode.INVALID_INPUT_MESSAGE + " |\n";
}
