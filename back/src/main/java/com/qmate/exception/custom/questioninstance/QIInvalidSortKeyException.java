package com.qmate.exception.custom.questioninstance;

import com.qmate.exception.BusinessGlobalException;
import com.qmate.exception.errorcode.QuestionInstanceErrorCode;

public class QIInvalidSortKeyException extends BusinessGlobalException {

  public QIInvalidSortKeyException() {
    super(QuestionInstanceErrorCode.invalidSortKey());
  }

}
