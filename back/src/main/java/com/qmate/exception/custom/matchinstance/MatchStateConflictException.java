package com.qmate.exception.custom.matchinstance;

import com.qmate.exception.BusinessGlobalException;
import com.qmate.exception.MatchErrorCode;

public class MatchStateConflictException extends BusinessGlobalException {
  public MatchStateConflictException(){
    super(MatchErrorCode.matchStateConflict());
  }

}
