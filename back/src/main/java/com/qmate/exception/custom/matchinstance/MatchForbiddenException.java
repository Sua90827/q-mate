package com.qmate.exception.custom.matchinstance;

import com.qmate.exception.BusinessGlobalException;
import com.qmate.exception.MatchErrorCode;

public class MatchForbiddenException extends BusinessGlobalException {
  public MatchForbiddenException(){
    super(MatchErrorCode.matchForbidden());
  }

}
