package com.qmate.exception.custom.questioninstance;

import com.qmate.exception.BusinessGlobalException;
import com.qmate.exception.errorcode.AnswerErrorCode;

public class AnswerAlreadyExistsException extends BusinessGlobalException {

  public AnswerAlreadyExistsException() {
    super(AnswerErrorCode.answerAlreadyExists());
  }
}
