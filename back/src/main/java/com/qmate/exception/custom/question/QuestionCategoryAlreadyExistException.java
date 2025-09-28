package com.qmate.exception.custom.question;

import com.qmate.exception.BusinessGlobalException;
import com.qmate.exception.errorcode.QuestionCategoryErrorCode;

public class QuestionCategoryAlreadyExistException extends BusinessGlobalException {

  public QuestionCategoryAlreadyExistException() {
    super(QuestionCategoryErrorCode.categoryNameAlreadyExists());
  }

}
