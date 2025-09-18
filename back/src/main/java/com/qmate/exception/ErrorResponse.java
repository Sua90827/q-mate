package com.qmate.exception;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorResponse {

  private final String errorCode;
  private final String message;
  private final List<FieldErrorDetail> errors;

  // Validation 에러를 위한 내부 클래스
  @Getter
  @AllArgsConstructor
  public static class FieldErrorDetail {

    private final String field;
    private final String defaultMessage;
  }

  //일반 에러 처리를 위한 생성자 오버로딩
  public ErrorResponse(String errorCode, String message) {
    this(errorCode, message, null);
  }


}
