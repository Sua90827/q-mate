package com.qmate.exception.errorcode;

import com.qmate.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public class QuestionErrorCode extends ErrorCode {

  private static final String QUESTION_NOT_FOUND_MESSAGE = "해당 질문을 찾을 수 없습니다.";

  public static ErrorCode questionNotFound() {
    return new QuestionErrorCode(HttpStatus.NOT_FOUND, "Question_001", QUESTION_NOT_FOUND_MESSAGE);
  }

  private QuestionErrorCode(HttpStatus httpStatus, String code, String message) {
    super(httpStatus, code, message);
  }
}
