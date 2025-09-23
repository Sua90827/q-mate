package com.qmate.exception.custom.questioninstance;

import com.qmate.exception.BusinessGlobalException;
import com.qmate.exception.errorcode.AnswerErrorCode;

public class AnswerNotFoundException extends BusinessGlobalException {

  public AnswerNotFoundException() {
    super(AnswerErrorCode.answerNotFound());
  }
}
