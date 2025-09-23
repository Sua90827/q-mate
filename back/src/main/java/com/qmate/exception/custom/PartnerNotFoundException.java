package com.qmate.exception.custom;

import com.qmate.exception.BusinessGlobalException;
import com.qmate.exception.MatchErrorCode;

public class PartnerNotFoundException extends BusinessGlobalException {

  public PartnerNotFoundException() {
    super(MatchErrorCode.partnerNotFound());
  }

}
