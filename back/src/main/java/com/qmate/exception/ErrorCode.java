package com.qmate.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

  //일반적인 오류
  INVALID_INPUT(HttpStatus.BAD_REQUEST, "INVALID_INPUT","잘못된 입력입니다."),
  UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "인증에 실패했습니다."),
  FORBIDDEN(HttpStatus.FORBIDDEN, "FORBIDDEN", "접근 권한이 없습니다."),
  RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "RESOURCE_NOT_FOUND", "요청한 자원을 찾을 수 없습니다."),
  INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR", "서버 내부 오류가 발생했습니다."),

  //매칭 관련 오류
  INVITE_CODE_EXPIRED(HttpStatus.NOT_FOUND, "INVITE_CODE_EXPIRED", "초대 코드가 만료되었거나 유효하지 않습니다."),
  ALREADY_IN_MATCH(HttpStatus.CONFLICT, "ALREADY_IN_MATCH", "이미 매칭에 속해 있습니다.");

  private final HttpStatus httpStatus;  //HTTP 상태 코드
  private final String code;  //비즈니스 에러 코드
  private final String message; //사용자에게 보여줄 기본 메시지
}
