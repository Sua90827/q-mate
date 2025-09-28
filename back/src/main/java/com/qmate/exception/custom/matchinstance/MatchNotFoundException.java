package com.qmate.exception.custom.matchinstance;

import com.qmate.exception.BusinessGlobalException;
import com.qmate.exception.MatchErrorCode;

public class MatchNotFoundException extends BusinessGlobalException {

  public MatchNotFoundException() {
    super(MatchErrorCode.matchNotFound());
  }

}
