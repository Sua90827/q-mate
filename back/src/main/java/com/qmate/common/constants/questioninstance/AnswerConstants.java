package com.qmate.common.constants.questioninstance;

import com.qmate.common.constants.HttpStatusCode;
import com.qmate.exception.errorcode.AnswerErrorCode;
import com.qmate.exception.errorcode.QuestionInstanceErrorCode;

public class AnswerConstants {
  public static final int MAX_CONTENT_LENGTH = 100;

  // 유효성 검사 메시지
  public static final String CONTENT_NOT_BLANK_MESSAGE = "내용은 필수입니다.";
  public static final String CONTENT_SIZE_MESSAGE = "내용은 최대 " + MAX_CONTENT_LENGTH + "자까지 입력할 수 있습니다.";

  // api docs
  public static final String CREATE_MD =
      "특정 질문 인스턴스(QI)에 대해 사용자가 자신의 답변을 1회 제출합니다.\n\n"
          + "- 성공 시 Location 헤더로 QI 상세 리소스를 반환합니다.\n\n"
          + "### 에러 응답\n\n"
          + "| HTTP | errorCode | message |\n"
          + "|-----:|-----------|---------|\n"
          + "| " + HttpStatusCode.NOT_FOUND + " | " + QuestionInstanceErrorCode.QI_NOT_FOUND_ERROR_CODE + " | "
          + QuestionInstanceErrorCode.QI_NOT_FOUND_MESSAGE + " |\n"
          + "| " + HttpStatusCode.CONFLICT + " | " + AnswerErrorCode.ANSWER_ALREADY_EXISTS_ERROR_CODE + " | "
          + AnswerErrorCode.ANSWER_ALREADY_EXISTS_MESSAGE + " |\n"
          + "| " + HttpStatusCode.LOCKED + " | " + AnswerErrorCode.ANSWER_CANNOT_MODIFY_ERROR_CODE + " | "
          + AnswerErrorCode.ANSWER_CANNOT_MODIFY_MESSAGE + " |\n";

  public static final String UPDATE_MD =
      "본인이 제출한 답변을 수정합니다. 완료된 답변은 수정할 수 없습니다.\n\n"
          + "### 에러 응답\n\n"
          + "| HTTP | errorCode | message |\n"
          + "|-----:|-----------|---------|\n"
          + "| " + HttpStatusCode.NOT_FOUND + " | " + AnswerErrorCode.ANSWER_NOT_FOUND_ERROR_CODE + " | "
          + AnswerErrorCode.ANSWER_NOT_FOUND_MESSAGE + " |\n"
          + "| " + HttpStatusCode.LOCKED + " | " + AnswerErrorCode.ANSWER_CANNOT_MODIFY_ERROR_CODE + " | "
          + AnswerErrorCode.ANSWER_CANNOT_MODIFY_MESSAGE + " |\n";
}
