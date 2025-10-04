package com.qmate.exception.custom.notification;

import com.qmate.exception.BusinessGlobalException;
import com.qmate.exception.errorcode.NotificationErrorCode;

public class NotificationNotFoundException extends BusinessGlobalException {

  public NotificationNotFoundException() {
    super(NotificationErrorCode.notificationNotFound());
  }

}
