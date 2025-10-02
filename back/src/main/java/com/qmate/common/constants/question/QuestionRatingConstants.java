package com.qmate.common.constants.question;

import com.qmate.common.constants.HttpStatusCode;
import com.qmate.exception.errorcode.QuestionErrorCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class QuestionRatingConstants {

  // api 문서 메시지
  public static final String CREATE_MD =
      "특정 질문에 대한 평가를 생성합니다.\n\n"
          + "### 에러 응답\n\n"
          + "| HTTP | errorCode | message |\n"
          + "|-----:|-----|---------|\n"
          + "| " + HttpStatusCode.NOT_FOUND + " | " + QuestionErrorCode.QUESTION_NOT_FOUND_ERROR_CODE + " | "
          + QuestionErrorCode.QUESTION_NOT_FOUND_MESSAGE + " |\n"
          + "| " + HttpStatusCode.CONFLICT + " | " + QuestionErrorCode.DUPLICATE_QUESTION_RATING_ERROR_CODE + " | "
          + QuestionErrorCode.DUPLICATE_QUESTION_RATING_MESSAGE + " |\n";

}
