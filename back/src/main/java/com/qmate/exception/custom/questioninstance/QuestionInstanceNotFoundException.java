package com.qmate.exception.custom.questioninstance;

import com.qmate.exception.BusinessGlobalException;
import com.qmate.exception.errorcode.QuestionInstanceErrorCode;

public class QuestionInstanceNotFoundException extends BusinessGlobalException {
  public QuestionInstanceNotFoundException() {
    super(QuestionInstanceErrorCode.questionInstanceNotFound());
  }

}
