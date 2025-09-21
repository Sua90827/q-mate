package com.qmate.exception.errorcode;

import com.qmate.exception.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class QuestionInstanceErrorCode extends ErrorCode {

  private static final String INVALID_XOR_MESSAGE = "question_id 또는 custom_question_id 중 하나만 설정되어야 합니다.";
  private static final String DELIVERED_AT_REQUIRED_MESSAGE = "delivered_at은 저장 전 반드시 설정되어야 합니다.";

  public static ErrorCode invalidXor() {
    return new QuestionInstanceErrorCode(HttpStatus.BAD_REQUEST, "QI_001", INVALID_XOR_MESSAGE);
  }

  public static ErrorCode deliveredAtRequired() {
    return new QuestionInstanceErrorCode(HttpStatus.BAD_REQUEST, "QI_002", DELIVERED_AT_REQUIRED_MESSAGE);
  }

  private QuestionInstanceErrorCode(HttpStatus httpStatus, String code, String message) {
    super(httpStatus, code, message);
  }
}
