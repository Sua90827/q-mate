package com.qmate.common.constants.question;

import com.qmate.common.constants.HttpStatusCode;
import com.qmate.exception.MatchErrorCode;
import com.qmate.exception.errorcode.CustomQuestionErrorCode;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CustomQuestionConstants {

  // sort 키
  // 추가, 제거시 queryImpl의 스위치문 변경 요구 + 아래 api 문서용 설명 변경
  public static final String SORT_KEY_ID = "id";
  public static final String SORT_KEY_CREATED_AT = "createdAt";
  public static final String SORT_KEY_UPDATED_AT = "updatedAt";

  // api 문서 메시지
  public static final String CREATE_MD =
      "특정 매치에 대한 커스텀 질문을 생성합니다.\n\n"
          + "### 에러 응답\n\n"
          + "| HTTP | errorCode | message |\n"
          + "|-----:|-----------|---------|\n"
          + "| " + HttpStatusCode.NOT_FOUND + " | " + MatchErrorCode.MATCH_NOT_FOUND_ERROR_CODE + " | "
          + MatchErrorCode.MATCH_NOT_FOUND_MESSAGE + " |\n";

  public static final String UPDATE_MD =
      "특정 커스텀 질문을 수정합니다.\n\n"
          + "- 이미 질문 인스턴스가 생성되었다면 수정 불가\n\n"
          + "### 에러 응답\n\n"
          + "| HTTP | errorCode | message |\n"
          + "|-----:|-----------|---------|\n"
          + "| " + HttpStatusCode.NOT_FOUND + " | " + CustomQuestionErrorCode.CQ_NOT_FOUND_ERROR_CODE + " | "
          + CustomQuestionErrorCode.CQ_NOT_FOUND_MESSAGE + " |\n"
          + "| " + HttpStatusCode.LOCKED + " | " + CustomQuestionErrorCode.CQ_LOCKED_ERROR_CODE + " | "
          + CustomQuestionErrorCode.CQ_LOCKED_MESSAGE + " |\n";

  public static final String DELETE_MD =
      "특정 커스텀 질문을 삭제합니다.\n\n"
          + "- 이미 질문 인스턴스가 생성되었다면 삭제 불가\n\n"
          + "### 에러 응답\n\n"
          + "| HTTP | errorCode | message |\n"
          + "|-----:|-----------|---------|\n"
          + "| " + HttpStatusCode.NOT_FOUND + " | " + CustomQuestionErrorCode.CQ_NOT_FOUND_ERROR_CODE + " | "
          + CustomQuestionErrorCode.CQ_NOT_FOUND_MESSAGE + " |\n"
          + "| " + HttpStatusCode.LOCKED + " | " + CustomQuestionErrorCode.CQ_LOCKED_ERROR_CODE + " | "
          + CustomQuestionErrorCode.CQ_LOCKED_MESSAGE + " |\n";

  public static final String GET_ONE_MD =
      "특정 커스텀 질문을 조회합니다.\n\n"
          + "### 에러 응답\n\n"
          + "| HTTP | errorCode | message |\n"
          + "|-----:|-----------|---------|\n"
          + "| " + HttpStatusCode.NOT_FOUND + " | " + CustomQuestionErrorCode.CQ_NOT_FOUND_ERROR_CODE + " | "
          + CustomQuestionErrorCode.CQ_NOT_FOUND_MESSAGE + " |\n";

  public static final String LIST_MD =
      "작성자(호출 사용자)의 커스텀 질문을 페이지로 조회합니다.\n\n"
          + "### 에러 응답\n\n"
          + "| HTTP | errorCode | message |\n"
          + "|-----:|-----------|---------|\n"
          + "| " + HttpStatusCode.BAD_REQUEST + " | " + CustomQuestionErrorCode.CQ_INVALID_SORT_KEY_ERROR_CODE + " | "
          + CustomQuestionErrorCode.CQ_INVALID_SORT_KEY_MESSAGE + " |\n";

  public static final String SORT_DESCRIPTION = "정렬: `property,(asc|desc)` / 허용 키: `" +
      SORT_KEY_ID + "`, `" + SORT_KEY_CREATED_AT + "`, `" + SORT_KEY_UPDATED_AT + "`";

}
