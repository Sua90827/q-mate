package com.qmate.exception.custom.questioninstance;

import com.qmate.exception.BusinessGlobalException;
import com.qmate.exception.errorcode.AnswerErrorCode;

public class AnswerForbiddenException extends BusinessGlobalException {

  public AnswerForbiddenException() {
    super(AnswerErrorCode.answerForbidden());
  }

}
