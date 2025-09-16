package com.qmate.exception.custom;

import com.qmate.exception.BusinessGlobalException;
import com.qmate.exception.ErrorCode;

//매칭을 찾을 수 없음 예외.
public class MatchNotFoundException extends BusinessGlobalException {
  public MatchNotFoundException(){
    super(ErrorCode.RESOURCE_NOT_FOUND);
  }

}
