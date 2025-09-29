package com.qmate.common.constants.question;

import com.qmate.common.constants.HttpStatusCode;
import com.qmate.exception.errorcode.CustomQuestionErrorCode;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CustomQuestionConstants {

  // sort 키
  // 추가, 제거시 queryImpl의 스위치문 변경 요구 + 아래 api 문서용 설명 변경
  public static final String SORT_KEY_ID = "id";
  public static final String SORT_KEY_CREATED_AT = "createdAt";
  public static final String SORT_KEY_UPDATED_AT = "updatedAt";

  // api 문서 메시지
  public static final String LIST_MD =
      "작성자(호출 사용자)의 커스텀 질문을 페이지로 조회합니다.\n\n"
          + "### 에러 응답\n\n"
          + "| HTTP | errorCode | message |\n"
          + "|-----:|-----------|---------|\n"
          + "| " + HttpStatusCode.BAD_REQUEST + " | " + CustomQuestionErrorCode.CQ_INVALID_SORT_KEY_ERROR_CODE + " | "
          + CustomQuestionErrorCode.CQ_INVALID_SORT_KEY_MESSAGE + " |\n";

  public static final String SORT_DESCRIPTION = "정렬: `property,(asc|desc)` / 허용 키: `" +
      SORT_KEY_ID + "`, `" + SORT_KEY_CREATED_AT + "`, `" + SORT_KEY_UPDATED_AT + "`";

}
