package com.qmate.common.constants.question;

import com.qmate.common.constants.HttpStatusCode;
import com.qmate.exception.errorcode.QuestionCategoryErrorCode;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class QuestionCategoryConstants {

  // 상수
  public static final String DEFAULT_CATEGORY_NAME = "기본 카테고리";
  public static final int MAX_NAME_LENGTH = 100;

  // 유효성 검사 메시지
  public static final String NAME_NOT_BLANK_MESSAGE = "카테고리명은 필수입니다.";
  public static final String NAME_SIZE_MESSAGE = "카테고리명은 최대 " + MAX_NAME_LENGTH + "자까지 입력할 수 있습니다.";
  public static final String RELATION_TYPE_NOT_BLANK_MESSAGE = "대상 관계는 필수입니다.";

  // api 문서 메시지
  public static final String CREATE_MD =
      "새로운 질문 카테고리를 생성합니다.\n\n"
          + "### 에러 응답\n\n"
          + "| HTTP | errorCode | message |\n"
          + "|-----:|-----------|---------|\n"
          + "| " + HttpStatusCode.CONFLICT + " | " + QuestionCategoryErrorCode.QC_NAME_ALREADY_EXISTS_ERROR_CODE + " | "
          + QuestionCategoryErrorCode.QC_NAME_ALREADY_EXISTS_MESSAGE + " |\n";

  public static final String UPDATE_MD =
      "기존 질문 카테고리를 부분 수정합니다.\n\n"
      + "### 에러 응답\n\n"
      + "| HTTP | errorCode | message |\n"
      + "|-----:|-----|---------|\n"
      + "|" + HttpStatusCode.NOT_FOUND + " | " + QuestionCategoryErrorCode.QC_NOT_FOUND_ERROR_CODE + " | "
      + QuestionCategoryErrorCode.QC_NOT_FOUND_MESSAGE
      + "|\n"
      + "| " + HttpStatusCode.CONFLICT + " | " + QuestionCategoryErrorCode.QC_NAME_ALREADY_EXISTS_ERROR_CODE + " | "
      + QuestionCategoryErrorCode.QC_NAME_ALREADY_EXISTS_MESSAGE
      + "|\n";
}
