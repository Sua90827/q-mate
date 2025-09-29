package com.qmate.exception.errorcode;

import com.qmate.exception.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class AnswerErrorCode extends ErrorCode {

  public static final String ANSWER_NOT_FOUND_MESSAGE = "답변을 찾을 수 없습니다.";
  public static final String ANSWER_ALREADY_EXISTS_MESSAGE = "이미 답변이 존재합니다.";
  public static final String ANSWER_CANNOT_MODIFY_MESSAGE = "답변을 작성/수정할 수 없는 상태입니다.";

  // error code
  public static final String ANSWER_NOT_FOUND_ERROR_CODE = "ANSWER_001";
  public static final String ANSWER_ALREADY_EXISTS_ERROR_CODE = "ANSWER_002";
  public static final String ANSWER_CANNOT_MODIFY_ERROR_CODE = "ANSWER_003";

  public static ErrorCode answerNotFound() {
    return new AnswerErrorCode(HttpStatus.NOT_FOUND, ANSWER_NOT_FOUND_ERROR_CODE, ANSWER_NOT_FOUND_MESSAGE);
  }

  public static ErrorCode answerAlreadyExists() {
    return new AnswerErrorCode(HttpStatus.CONFLICT, ANSWER_ALREADY_EXISTS_ERROR_CODE, ANSWER_ALREADY_EXISTS_MESSAGE);
  }

  public static ErrorCode answerCannotModify() {
    return new AnswerErrorCode(HttpStatus.LOCKED, ANSWER_CANNOT_MODIFY_ERROR_CODE, ANSWER_CANNOT_MODIFY_MESSAGE);
  }

  protected AnswerErrorCode(HttpStatus httpStatus, String code, String message) {
    super(httpStatus, code, message);
  }
}
