package com.qmate.exception.errorcode;

import com.qmate.exception.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class EventErrorCode extends ErrorCode {

  // message
  public static final String EVENT_NOT_FOUND_MESSAGE = "일정을 찾을 수 없습니다.";
  public static final String EVENT_REPEAT_MODIFICATION_NOT_ALLOWED_MESSAGE = "기념일의 반복 설정은 변경할 수 없습니다.";
  public static final String EVENT_DELETION_NOT_ALLOWED_MESSAGE = "기념일은 삭제할 수 없습니다.";

  // error code
  public static final String EVENT_NOT_FOUND_ERROR_CODE = "EVENT_001";
  public static final String EVENT_REPEAT_MODIFICATION_NOT_ALLOWED_ERROR_CODE = "EVENT_002";
  public static final String EVENT_DELETION_NOT_ALLOWED_ERROR_CODE = "EVENT_003";

  // factory method
  public static ErrorCode eventNotFound() {
    return new EventErrorCode(HttpStatus.NOT_FOUND, EVENT_NOT_FOUND_ERROR_CODE, EVENT_NOT_FOUND_MESSAGE);
  }

  public static ErrorCode eventRepeatModificationNotAllowed() {
    return new EventErrorCode(HttpStatus.CONFLICT, EVENT_REPEAT_MODIFICATION_NOT_ALLOWED_ERROR_CODE,
        EVENT_REPEAT_MODIFICATION_NOT_ALLOWED_MESSAGE);
  }

  public static ErrorCode eventDeletionNotAllowed() {
    return new EventErrorCode(HttpStatus.CONFLICT, EVENT_DELETION_NOT_ALLOWED_ERROR_CODE,
        EVENT_DELETION_NOT_ALLOWED_MESSAGE);
  }

  protected EventErrorCode(HttpStatus httpStatus, String code, String message) {
    super(httpStatus, code, message);
  }
}
