package com.qmate.exception.custom.question;

import com.qmate.exception.BusinessGlobalException;
import com.qmate.exception.errorcode.QuestionCategoryErrorCode;

public class QuestionCategoryNotFoundException extends BusinessGlobalException {

  public QuestionCategoryNotFoundException() {
    super(QuestionCategoryErrorCode.categoryNotFound());
  }

}
