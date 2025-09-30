package com.qmate.common.constants.questioninstance;

import com.qmate.common.constants.HttpStatusCode;
import com.qmate.exception.errorcode.QuestionInstanceErrorCode;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class QuestionInstanceConstants {

  // sort 키
  // 추가, 제거시 queryImpl의 변경 요함 + 아래 api 문서용 설명 변경
  public static final String SORT_KEY_DELIVERED_AT = "deliveredAt";
  public static final String SORT_KEY_COMPLETED_AT = "completedAt";
  public static final String SORT_KEY_STATUS = "status";

  // api 문서 메시지
  public static final String DETAIL_MD =
      "단일 질문 인스턴스의 상세 정보를 반환합니다.\n\n"
          + "- 요청자가 참여중인 Match의 질문 인스턴스가 아니라면 조회 불가\n\n"
          + "### 에러 응답\n\n"
          + "| HTTP | errorCode | message |\n"
          + "|-----:|-----------|---------|\n"
          + "| " + HttpStatusCode.NOT_FOUND + " | " + QuestionInstanceErrorCode.QI_NOT_FOUND_ERROR_CODE + " | "
          + QuestionInstanceErrorCode.QI_NOT_FOUND_MESSAGE + " |\n";

  public static final String TODAY_MD =
      "해당 매칭에서 가장 최근에 알림(delivered)된 질문 인스턴스를 상세 형태로 반환합니다.\n\n"
          + "- 의미: 엔드포인트 명은 today지만, 날짜 경계와 무관하게 `delivered_at`이 가장 최신인 1건을 반환\n\n"
          + "- 요청자가 참여중인 Match의 질문 인스턴스가 아니라면 조회 불가\n\n"
          + "### 에러 응답\n\n"
          + "| HTTP | errorCode | message |\n"
          + "|-----:|-----------|---------|\n"
          + "| " + HttpStatusCode.NOT_FOUND + " | " + QuestionInstanceErrorCode.QI_NOT_FOUND_ERROR_CODE + " | "
          + QuestionInstanceErrorCode.QI_NOT_FOUND_MESSAGE + " |\n";

  public static final String LIST_MD =
      "특정 매치에 속한 질문 인스턴스들의 페이징된 목록을 반환합니다.\n\n"
          + "- 요청자가 참여중인 Match의 질문 인스턴스가 아니라면 조회 불가\n\n"
          + "### 에러 응답\n\n"
          + "| HTTP | errorCode | message |\n"
          + "|-----:|-----------|---------|\n"
          + "| " + HttpStatusCode.BAD_REQUEST + " | " + QuestionInstanceErrorCode.QI_INVALID_SORT_KEY_ERROR_CODE + " | "
          + QuestionInstanceErrorCode.QI_INVALID_SORT_KEY_MESSAGE + " |\n";

  public static final String SORT_DESCRIPTION = "정렬: `property,(asc|desc)` / 허용 키: `" +
      SORT_KEY_DELIVERED_AT + "`, `" + SORT_KEY_COMPLETED_AT + "`, `" + SORT_KEY_STATUS + "`";
}
