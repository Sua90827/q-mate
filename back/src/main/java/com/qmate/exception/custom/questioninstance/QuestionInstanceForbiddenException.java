package com.qmate.exception.custom.questioninstance;

import com.qmate.exception.BusinessGlobalException;
import com.qmate.exception.errorcode.QuestionInstanceErrorCode;

public class QuestionInstanceForbiddenException extends BusinessGlobalException {
  public QuestionInstanceForbiddenException() {
    super(QuestionInstanceErrorCode.forbiddenToAccess());
  }

}
