package com.qmate.exception.custom.event;

import com.qmate.exception.BusinessGlobalException;
import com.qmate.exception.errorcode.EventErrorCode;

public class EventDeletionNotAllowedException extends BusinessGlobalException {

  public EventDeletionNotAllowedException() {
    super(EventErrorCode.eventDeletionNotAllowed());
  }

}
