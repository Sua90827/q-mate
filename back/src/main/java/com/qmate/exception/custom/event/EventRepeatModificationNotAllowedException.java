package com.qmate.exception.custom.event;

import com.qmate.exception.BusinessGlobalException;
import com.qmate.exception.errorcode.EventErrorCode;

public class EventRepeatModificationNotAllowedException extends BusinessGlobalException {

  public EventRepeatModificationNotAllowedException() {
    super(EventErrorCode.eventRepeatModificationNotAllowed());
  }

}
