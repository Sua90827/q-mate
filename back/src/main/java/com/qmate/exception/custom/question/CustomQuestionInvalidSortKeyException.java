package com.qmate.exception.custom.question;

import com.qmate.exception.BusinessGlobalException;
import com.qmate.exception.errorcode.CustomQuestionErrorCode;

public class CustomQuestionInvalidSortKeyException extends BusinessGlobalException {

  public CustomQuestionInvalidSortKeyException() {
    super(CustomQuestionErrorCode.customQuestionInvalidSortKey());
  }
}
