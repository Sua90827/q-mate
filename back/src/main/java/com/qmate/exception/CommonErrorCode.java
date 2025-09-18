package com.qmate.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

//도메인에 종속되지 않는 공통 에러 코드들을 정의하는 클래스.

@Getter
public class CommonErrorCode extends ErrorCode {

  // 메시지 상수
  private static final String INVALID_INPUT_MESSAGE = "잘못된 입력 값입니다.";
  private static final String UNAUTHORIZED_MESSAGE = "인증에 실패했습니다.";
  private static final String FORBIDDEN_MESSAGE = "접근 권한이 없습니다.";
  private static final String INTERNAL_SERVER_ERROR_MESSAGE = "서버 내부 오류가 발생했습니다.";

  // 에러 코드 객체 반환 메서드
  public static ErrorCode invalidInput() {
    return new CommonErrorCode(HttpStatus.BAD_REQUEST, "COMMON_001", INVALID_INPUT_MESSAGE);
  }

  public static ErrorCode unauthorized() {
    return new CommonErrorCode(HttpStatus.UNAUTHORIZED, "COMMON_002", UNAUTHORIZED_MESSAGE);
  }

  public static ErrorCode forbidden() {
    return new CommonErrorCode(HttpStatus.FORBIDDEN, "COMMON_003", FORBIDDEN_MESSAGE);
  }

  public static ErrorCode internalServerError() {
    return new CommonErrorCode(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON_004",
        INTERNAL_SERVER_ERROR_MESSAGE);
  }

  private CommonErrorCode(HttpStatus httpStatus, String code, String message) {
    super(httpStatus, code, message);
  }
}