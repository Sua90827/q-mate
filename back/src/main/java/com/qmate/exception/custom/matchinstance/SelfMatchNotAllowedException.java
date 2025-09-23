package com.qmate.exception.custom.matchinstance;

import com.qmate.exception.BusinessGlobalException;
import com.qmate.exception.MatchErrorCode;

public class SelfMatchNotAllowedException extends BusinessGlobalException {
  public SelfMatchNotAllowedException(){
    super(MatchErrorCode.cannotMatchWithSelf());
  }

}
