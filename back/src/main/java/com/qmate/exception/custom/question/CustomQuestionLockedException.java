package com.qmate.exception.custom.question;

import com.qmate.exception.BusinessGlobalException;
import com.qmate.exception.errorcode.CustomQuestionErrorCode;

public class CustomQuestionLockedException extends BusinessGlobalException {

  public CustomQuestionLockedException() {
    super(CustomQuestionErrorCode.customQuestionLocked());
  }

}
