package com.qmate.exception;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class ErrorCode {


  private final HttpStatus httpStatus;  //HTTP 상태 코드
  private final String code;  //비즈니스 에러 코드
  private final String message; //사용자에게 보여줄 기본 메시지
}
