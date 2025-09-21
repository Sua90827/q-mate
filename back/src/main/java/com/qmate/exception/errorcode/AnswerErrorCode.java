package com.qmate.exception.errorcode;

import com.qmate.exception.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class AnswerErrorCode extends ErrorCode {

  private static final String ANSWER_NOT_FOUND = "답변을 찾을 수 없습니다.";
  public static final String ANSWER_ALREADY_EXISTS = "이미 답변이 존재합니다.";
  public static final String ANSWER_CANNOT_MODIFY = "답변을 작성/수정할 수 없는 상태입니다.";

  public static ErrorCode answerNotFound() {
    return new AnswerErrorCode(HttpStatus.NOT_FOUND, "ANSWER_001", ANSWER_NOT_FOUND);
  }

  public static ErrorCode answerAlreadyExists() {
    return new AnswerErrorCode(HttpStatus.CONFLICT, "ANSWER_002", ANSWER_ALREADY_EXISTS);
  }

  public static ErrorCode answerCannotModify() {
    return new AnswerErrorCode(HttpStatus.BAD_REQUEST, "ANSWER_003", ANSWER_CANNOT_MODIFY);
  }

  protected AnswerErrorCode(HttpStatus httpStatus, String code, String message) {
    super(httpStatus, code, message);
  }
}
