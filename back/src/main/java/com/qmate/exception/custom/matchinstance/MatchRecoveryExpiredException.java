package com.qmate.exception.custom.matchinstance;

import com.qmate.exception.BusinessGlobalException;
import com.qmate.exception.MatchErrorCode;

public class MatchRecoveryExpiredException extends BusinessGlobalException {
  public MatchRecoveryExpiredException(){
    super(MatchErrorCode.matchRecoveryExpired());
  }

}
