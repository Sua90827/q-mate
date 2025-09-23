package com.qmate.exception.errorcode;

import com.qmate.exception.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class QuestionInstanceErrorCode extends ErrorCode {

  private static final String INVALID_XOR_MESSAGE = "question_id 또는 custom_question_id 중 하나만 설정되어야 합니다.";
  private static final String DELIVERED_AT_REQUIRED_MESSAGE = "delivered_at은 저장 전 반드시 설정되어야 합니다.";
  private static final String QUESTION_INSTANCE_NOT_FOUND_MESSAGE = "해당 질문 인스턴스를 찾을 수 없습니다.";
  private static final String FORBIDDEN_TO_ACCESS_MESSAGE = "해당 질문 인스턴스에 접근할 권한이 없습니다.";

  public static ErrorCode invalidXor() {
    return new QuestionInstanceErrorCode(HttpStatus.BAD_REQUEST, "QI_001", INVALID_XOR_MESSAGE);
  }

  public static ErrorCode deliveredAtRequired() {
    return new QuestionInstanceErrorCode(HttpStatus.BAD_REQUEST, "QI_002", DELIVERED_AT_REQUIRED_MESSAGE);
  }

  public static ErrorCode questionInstanceNotFound() {
    return new QuestionInstanceErrorCode(HttpStatus.NOT_FOUND, "QI_003", QUESTION_INSTANCE_NOT_FOUND_MESSAGE);
  }

  public static ErrorCode forbiddenToAccess() {
    return new QuestionInstanceErrorCode(HttpStatus.FORBIDDEN, "QI_004", FORBIDDEN_TO_ACCESS_MESSAGE);
  }

  private QuestionInstanceErrorCode(HttpStatus httpStatus, String code, String message) {
    super(httpStatus, code, message);
  }
}
