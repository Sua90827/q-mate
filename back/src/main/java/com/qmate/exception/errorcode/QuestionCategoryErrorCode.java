package com.qmate.exception.errorcode;

import com.qmate.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public class QuestionCategoryErrorCode extends ErrorCode {

  // message
  public static final String QC_NOT_FOUND_MESSAGE = "해당 질문 카테고리를 찾을 수 없습니다.";
  public static final String QC_NAME_ALREADY_EXISTS_MESSAGE = "이미 존재하는 카테고리 이름입니다.";

  // error code
  public static final String QC_NOT_FOUND_ERROR_CODE = "QC_001";
  public static final String QC_NAME_ALREADY_EXISTS_ERROR_CODE = "QC_002";

  public static ErrorCode categoryNotFound() {
    return new QuestionCategoryErrorCode(HttpStatus.NOT_FOUND, QC_NOT_FOUND_ERROR_CODE, QC_NOT_FOUND_MESSAGE);
  }

  public static ErrorCode categoryNameAlreadyExists() {
    return new QuestionCategoryErrorCode(HttpStatus.CONFLICT, QC_NAME_ALREADY_EXISTS_ERROR_CODE, QC_NAME_ALREADY_EXISTS_MESSAGE);
  }

  private QuestionCategoryErrorCode(HttpStatus httpStatus, String code, String message) {
    super(httpStatus, code, message);
  }

}
