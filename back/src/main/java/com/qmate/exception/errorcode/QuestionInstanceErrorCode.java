package com.qmate.exception.errorcode;

import com.qmate.exception.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class QuestionInstanceErrorCode extends ErrorCode {

  public static final String QI_INVALID_XOR_MESSAGE = "question_id 또는 custom_question_id 중 하나만 설정되어야 합니다.";
  public static final String QI_NOT_FOUND_MESSAGE = "해당 질문 인스턴스를 찾을 수 없습니다.";
  public static final String FORBIDDEN_TO_ACCESS_MESSAGE = "해당 질문 인스턴스에 접근할 권한이 없습니다.";
  public static final String QI_INVALID_SORT_KEY_MESSAGE = "허용되지 않은 정렬 키 입니다.";

  // error code
  public static final String QI_NOT_FOUND_ERROR_CODE = "QI_001";
  public static final String QI_INVALID_XOR_ERROR_CODE = "QI_002";
  public static final String QI_INVALID_SORT_KEY_ERROR_CODE = "QI_003";

  public static ErrorCode questionInstanceNotFound() {
    return new QuestionInstanceErrorCode(HttpStatus.NOT_FOUND, QI_NOT_FOUND_ERROR_CODE, QI_NOT_FOUND_MESSAGE);
  }

  public static ErrorCode invalidXor() {
    return new QuestionInstanceErrorCode(HttpStatus.BAD_REQUEST, QI_INVALID_XOR_ERROR_CODE, QI_INVALID_XOR_MESSAGE);
  }

  public static ErrorCode invalidSortKey() {
    return new QuestionInstanceErrorCode(HttpStatus.BAD_REQUEST, QI_INVALID_SORT_KEY_ERROR_CODE, QI_INVALID_SORT_KEY_MESSAGE);
  }

  public static ErrorCode forbiddenToAccess() {
    return new QuestionInstanceErrorCode(HttpStatus.FORBIDDEN, "QI_004", FORBIDDEN_TO_ACCESS_MESSAGE);
  }


  private QuestionInstanceErrorCode(HttpStatus httpStatus, String code, String message) {
    super(httpStatus, code, message);
  }
}
