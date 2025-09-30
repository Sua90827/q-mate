package com.qmate.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

//도메인에 종속되지 않는 공통 에러 코드들을 정의하는 클래스.

@Getter
public class CommonErrorCode extends ErrorCode {

  // 메시지 상수
  public static final String INVALID_INPUT_MESSAGE = "잘못된 입력 값입니다.";
  public static final String UNAUTHORIZED_MESSAGE = "인증에 실패했습니다.";
  public static final String FORBIDDEN_MESSAGE = "접근 권한이 없습니다.";
  public static final String INTERNAL_SERVER_ERROR_MESSAGE = "서버 내부 오류가 발생했습니다.";

  // 에러 코드
  public static final String INVALID_INPUT_ERROR_CODE = "COMMON_001";
  public static final String UNAUTHORIZED_ERROR_CODE = "COMMON_002";
  public static final String FORBIDDEN_ERROR_CODE = "COMMON_003";
  public static final String INTERNAL_SERVER_ERROR_CODE = "COMMON_004";

  // 에러 코드 객체 반환 메서드
  public static ErrorCode invalidInput() {
    return new CommonErrorCode(HttpStatus.BAD_REQUEST, INVALID_INPUT_ERROR_CODE, INVALID_INPUT_MESSAGE);
  }

  public static ErrorCode unauthorized() {
    return new CommonErrorCode(HttpStatus.UNAUTHORIZED, UNAUTHORIZED_ERROR_CODE, UNAUTHORIZED_MESSAGE);
  }

  public static ErrorCode forbidden() {
    return new CommonErrorCode(HttpStatus.FORBIDDEN, FORBIDDEN_ERROR_CODE, FORBIDDEN_MESSAGE);
  }

  public static ErrorCode internalServerError() {
    return new CommonErrorCode(HttpStatus.INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR_CODE,
        INTERNAL_SERVER_ERROR_MESSAGE);
  }

  private CommonErrorCode(HttpStatus httpStatus, String code, String message) {
    super(httpStatus, code, message);
  }
}