package com.qmate.exception.custom.question;

import com.qmate.exception.BusinessGlobalException;
import com.qmate.exception.errorcode.CustomQuestionErrorCode;

public class CustomQuestionNotFoundException extends BusinessGlobalException {

  public CustomQuestionNotFoundException() {
    super(CustomQuestionErrorCode.customQuestionNotFound());
  }

}
