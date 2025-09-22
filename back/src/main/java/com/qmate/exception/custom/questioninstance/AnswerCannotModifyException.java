package com.qmate.exception.custom.questioninstance;

import com.qmate.exception.BusinessGlobalException;
import com.qmate.exception.errorcode.AnswerErrorCode;

public class AnswerCannotModifyException extends BusinessGlobalException {

  public AnswerCannotModifyException() {
    super(AnswerErrorCode.answerCannotModify());
  }

}
