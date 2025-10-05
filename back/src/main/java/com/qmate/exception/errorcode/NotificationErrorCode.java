package com.qmate.exception.errorcode;

import com.qmate.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public class NotificationErrorCode extends ErrorCode {

  // message
  public static final String NOTIFICATION_NOT_FOUND_MESSAGE = "알림이 존재하지 않습니다.";

  // error code
  public static final String NOTIFICATION_NOT_FOUND_ERROR_CODE = "NOTIFICATION-001";

  // instances
  public static ErrorCode notificationNotFound() {
    return new NotificationErrorCode(HttpStatus.NOT_FOUND, NOTIFICATION_NOT_FOUND_ERROR_CODE, NOTIFICATION_NOT_FOUND_MESSAGE);
  }

  private NotificationErrorCode(HttpStatus httpStatus, String code, String message) {
    super(httpStatus, code, message);
  }
}
