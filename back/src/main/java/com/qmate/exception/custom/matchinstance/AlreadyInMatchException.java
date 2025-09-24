package com.qmate.exception.custom.matchinstance;

import com.qmate.exception.BusinessGlobalException;
import com.qmate.exception.MatchErrorCode;

//이미 매칭 참여 예외
public class AlreadyInMatchException extends BusinessGlobalException {

  public AlreadyInMatchException() {
    super(MatchErrorCode.alreadyInMatch());
  }

}
