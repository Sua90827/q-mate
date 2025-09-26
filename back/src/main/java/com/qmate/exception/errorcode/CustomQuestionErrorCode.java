package com.qmate.exception.errorcode;

import com.qmate.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public class CustomQuestionErrorCode extends ErrorCode {

  private static final String CUSTOM_QUESTION_NOT_FOUND_MESSAGE = "해당 커스텀 질문을 찾을 수 없습니다.";
  private static final String CUSTOM_QUESTION_FORBIDDEN_MESSAGE = "본인이 생성한 질문만 수정/삭제할 수 있습니다.";

  public static ErrorCode customQuestionNotFound() {
    return new CustomQuestionErrorCode(HttpStatus.NOT_FOUND, "CQ_001", CUSTOM_QUESTION_NOT_FOUND_MESSAGE);
  }

  public static ErrorCode customQuestionForbidden() {
    return new CustomQuestionErrorCode(HttpStatus.FORBIDDEN, "CQ_002", CUSTOM_QUESTION_FORBIDDEN_MESSAGE);
  }

  private CustomQuestionErrorCode(HttpStatus httpStatus, String code, String message) {
    super(httpStatus, code, message);
  }

}
