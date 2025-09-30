package com.qmate.exception.errorcode;

import com.qmate.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public class CustomQuestionErrorCode extends ErrorCode {

  // message
  public static final String CQ_NOT_FOUND_MESSAGE = "해당 커스텀 질문을 찾을 수 없습니다.";
  public static final String CQ_LOCKED_MESSAGE = "커스텀 질문을 수정/삭제할 수 없는 상태입니다.";
  public static final String CQ_INVALID_SORT_KEY_MESSAGE = "허용되지 않은 정렬 키 입니다.";

  // error code
  public static final String CQ_NOT_FOUND_ERROR_CODE = "CQ_001";
  public static final String CQ_LOCKED_ERROR_CODE = "CQ_002";
  public static final String CQ_INVALID_SORT_KEY_ERROR_CODE = "CQ_003";

  public static ErrorCode customQuestionNotFound() {
    return new CustomQuestionErrorCode(HttpStatus.NOT_FOUND, CQ_NOT_FOUND_ERROR_CODE, CQ_NOT_FOUND_MESSAGE);
  }

  public static ErrorCode customQuestionLocked() {
    return new CustomQuestionErrorCode(HttpStatus.FORBIDDEN, CQ_LOCKED_ERROR_CODE, CQ_LOCKED_MESSAGE);
  }

  public static ErrorCode customQuestionInvalidSortKey() {
    return new CustomQuestionErrorCode(HttpStatus.BAD_REQUEST, CQ_INVALID_SORT_KEY_ERROR_CODE, CQ_INVALID_SORT_KEY_MESSAGE);
  }

  private CustomQuestionErrorCode(HttpStatus httpStatus, String code, String message) {
    super(httpStatus, code, message);
  }

}
