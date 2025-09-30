package com.qmate.exception.errorcode;

import com.qmate.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public class QuestionErrorCode extends ErrorCode {

  // message
  public static final String QUESTION_NOT_FOUND_MESSAGE = "해당 질문을 찾을 수 없습니다.";
  public static final String DUPLICATE_QUESTION_RATING_MESSAGE = "해당 질문에 이미 평가를 남겼습니다.";

  // error code
  public static final String QUESTION_NOT_FOUND_ERROR_CODE = "QUESTION_001";
  public static final String DUPLICATE_QUESTION_RATING_ERROR_CODE = "QUESTION_002";

  public static ErrorCode questionNotFound() {
    return new QuestionErrorCode(HttpStatus.NOT_FOUND, QUESTION_NOT_FOUND_ERROR_CODE, QUESTION_NOT_FOUND_MESSAGE);
  }
  public static ErrorCode duplicateQuestionRating() {
    return new QuestionErrorCode(HttpStatus.CONFLICT, DUPLICATE_QUESTION_RATING_ERROR_CODE, DUPLICATE_QUESTION_RATING_MESSAGE);
  }

  private QuestionErrorCode(HttpStatus httpStatus, String code, String message) {
    super(httpStatus, code, message);
  }
}
