package com.qmate.exception.custom.event;

import com.qmate.exception.BusinessGlobalException;
import com.qmate.exception.errorcode.EventErrorCode;

public class EventNotFoundException extends BusinessGlobalException {

  public EventNotFoundException() {
    super(EventErrorCode.eventNotFound());
  }
}
