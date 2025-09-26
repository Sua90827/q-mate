package com.qmate.exception.custom.question;

import com.qmate.exception.BusinessGlobalException;
import com.qmate.exception.errorcode.QuestionErrorCode;

public class QuestionNotFoundException extends BusinessGlobalException {

  public QuestionNotFoundException() {
    super(QuestionErrorCode.questionNotFound());
  }

}
