package com.qmate.exception.custom.questioninstance;

import com.qmate.exception.BusinessGlobalException;
import com.qmate.exception.errorcode.QuestionInstanceErrorCode;

public class QuestionInstanceDeliveredAtRequiredException extends BusinessGlobalException {

  public QuestionInstanceDeliveredAtRequiredException() {
    super(QuestionInstanceErrorCode.deliveredAtRequired());
  }

}
