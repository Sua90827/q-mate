package com.qmate.exception.custom.event;

import com.qmate.exception.BusinessGlobalException;
import com.qmate.exception.errorcode.EventErrorCode;

public class EventListDateRangeExceededException extends BusinessGlobalException {

  public EventListDateRangeExceededException() {
    super(EventErrorCode.eventListDateRangeExceeded());
  }

}
