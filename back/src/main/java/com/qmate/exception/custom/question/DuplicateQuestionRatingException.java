package com.qmate.exception.custom.question;

import com.qmate.exception.BusinessGlobalException;
import com.qmate.exception.errorcode.QuestionErrorCode;

public class DuplicateQuestionRatingException extends BusinessGlobalException {

  public DuplicateQuestionRatingException() {
    super(QuestionErrorCode.duplicateQuestionRating());
  }

}
