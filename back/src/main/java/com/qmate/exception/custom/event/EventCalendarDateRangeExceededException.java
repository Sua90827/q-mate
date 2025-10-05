package com.qmate.exception.custom.event;

import com.qmate.exception.BusinessGlobalException;
import com.qmate.exception.errorcode.EventErrorCode;

public class EventCalendarDateRangeExceededException extends BusinessGlobalException {

  public EventCalendarDateRangeExceededException() {
    super(EventErrorCode.eventCalendarDateRangeExceeded());
  }

}
