package com.qmate.exception.custom.matchinstance;

import com.qmate.exception.BusinessGlobalException;
import com.qmate.exception.MatchErrorCode;

//연인 관계 기념일 필수 입력 예외
public class InvalidStartDateForCoupleException extends BusinessGlobalException {

  public InvalidStartDateForCoupleException() {
    super(MatchErrorCode.invalidStartDateForCouple());
  }


}
