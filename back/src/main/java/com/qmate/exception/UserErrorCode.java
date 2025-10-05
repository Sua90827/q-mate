package com.qmate.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class UserErrorCode extends ErrorCode {

  //에러 메시지 코드 상수
  public static final String USER_NOT_FOUND_MESSAGE = "해당 사용자를 찾을 수 없습니다.";

  //에러 코드 상수
  public static final String USER_NOT_FOUND_ERROR_CODE = "USER_001";


  //에러 코드 객체 반환 메서드
  public static ErrorCode userNotFound() {
    return new UserErrorCode(HttpStatus.NOT_FOUND, USER_NOT_FOUND_ERROR_CODE, USER_NOT_FOUND_MESSAGE);

  }


  private UserErrorCode(HttpStatus httpStatus, String code, String message) {
    super(httpStatus, code, message);
  }

}
