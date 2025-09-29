package com.qmate.common.constants.question;

import com.qmate.common.constants.HttpStatusCode;
import com.qmate.exception.errorcode.QuestionErrorCode;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class QuestionConstants {

  // 상수
  public static final int MAX_TEXT_LENGTH = 500;

  // 유효성 검사 메시지
  public static final String CATEGORY_ID_NOT_NULL_MESSAGE = "카테고리 ID는 필수입니다.";
  public static final String RELATION_TYPE_NOT_BLANK_MESSAGE = "대상 관계는 필수입니다.";
  public static final String TEXT_NOT_BLANK_MESSAGE = "질문 내용은 필수입니다.";
  public static final String TEXT_SIZE_MESSAGE = "질문은 최대 " + MAX_TEXT_LENGTH + "자까지 입력할 수 있습니다.";

  // api 문서 메시지
  public static final String UPDATE_MD =
      "기존 질문을 부분 수정합니다.\n\n"
          + "### 에러 응답\n\n"
          + "| HTTP | errorCode | message |\n"
          + "|-----:|-----|---------|\n"
          + "| " + HttpStatusCode.NOT_FOUND + " | " + QuestionErrorCode.QUESTION_NOT_FOUND_ERROR_CODE + " | "
          + QuestionErrorCode.QUESTION_NOT_FOUND_MESSAGE + " |\n";
}
