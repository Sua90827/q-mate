package com.qmate.exception.errorcode;

import com.qmate.exception.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class EventErrorCode extends ErrorCode {

  // message
  public static final String EVENT_NOT_FOUND_MESSAGE = "일정을 찾을 수 없습니다.";

  // error code
  public static final String EVENT_NOT_FOUND_ERROR_CODE = "EVENT_001";

  // factory method
  public static ErrorCode eventNotFound() {
    return new EventErrorCode(HttpStatus.NOT_FOUND, EVENT_NOT_FOUND_ERROR_CODE, EVENT_NOT_FOUND_MESSAGE);
  }

  protected EventErrorCode(HttpStatus httpStatus, String code, String message) {
    super(httpStatus, code, message);
  }
}
