package com.qmate.exception.errorcode;

import com.qmate.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public class QuestionCategoryErrorCode extends ErrorCode {

  private static final String CATEGORY_NOT_FOUND_MESSAGE = "해당 질문 카테고리를 찾을 수 없습니다.";
  private static final String CATEGORY_NAME_ALREADY_EXISTS_MESSAGE = "이미 존재하는 카테고리 이름입니다.";

  public static ErrorCode categoryNotFound() {
    return new QuestionCategoryErrorCode(HttpStatus.NOT_FOUND, "QC_001", CATEGORY_NOT_FOUND_MESSAGE);
  }

  public static ErrorCode categoryNameAlreadyExists() {
    return new QuestionCategoryErrorCode(HttpStatus.CONFLICT, "QC_002", CATEGORY_NAME_ALREADY_EXISTS_MESSAGE);
  }

  private QuestionCategoryErrorCode(HttpStatus httpStatus, String code, String message) {
    super(httpStatus, code, message);
  }

}
