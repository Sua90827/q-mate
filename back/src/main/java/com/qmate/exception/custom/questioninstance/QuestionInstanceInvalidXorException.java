package com.qmate.exception.custom.questioninstance;

import com.qmate.exception.BusinessGlobalException;
import com.qmate.exception.errorcode.QuestionInstanceErrorCode;

// question/customQuestion 동시/둘다-null
public class QuestionInstanceInvalidXorException extends BusinessGlobalException {

  public QuestionInstanceInvalidXorException() {
    super(QuestionInstanceErrorCode.invalidXor());
  }

}
