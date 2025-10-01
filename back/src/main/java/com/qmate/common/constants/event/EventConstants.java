package com.qmate.common.constants.event;

import com.qmate.common.constants.HttpStatusCode;
import com.qmate.exception.MatchErrorCode;
import com.qmate.exception.errorcode.CustomQuestionErrorCode;
import com.qmate.exception.errorcode.EventErrorCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class EventConstants {

  // 상수
  public static final int EVENT_TITLE_MAX_LENGTH = 120;
  public static final int EVENT_DESCRIPTION_MAX_LENGTH = 1000;

  // validation message
  // 제목 공백 불가
  public static final String EVENT_TITLE_NOT_BLANK_MESSAGE = "일정 제목은 필수 입력 값입니다.";
  public static final String EVENT_TITLE_SIZE_MESSAGE = "일정 제목은 최대 " + EVENT_TITLE_MAX_LENGTH + "자까지 가능합니다.";
  public static final String EVENT_DESCRIPTION_SIZE_MESSAGE = "일정 설명은 최대 " + EVENT_DESCRIPTION_MAX_LENGTH + "자까지 가능합니다.";

  // api doc
  // TODO MatchErrorCode 변경 시 참조 필요
  public static final String CREATE_MD =
      "매치 하위에 일정을 생성합니다.\n\n"
          + "- matchId, userId(인증)로 권한과 존재를 함께 검증합니다.\n\n"
          + "### 에러 응답\n\n"
          + "| HTTP | errorCode | message |\n"
          + "|-----:|-----------|---------|\n"
          + "| " + HttpStatusCode.NOT_FOUND + " | " + "MATCH_002" + " | "
          + "해당 매칭을 찾을 수 없습니다." + " |\n";

  public static final String GET_DETAIL_MD =
      "일정을 조회합니다.\n\n"
          + "- matchId, userId(인증), eventId로 권한과 존재를 함께 검증합니다.\n\n"
          + "### 에러 응답\n\n"
          + "| HTTP | errorCode | message |\n"
          + "|-----:|-----------|---------|\n"
          + "| " + HttpStatusCode.NOT_FOUND + " | " + EventErrorCode.EVENT_NOT_FOUND_ERROR_CODE + " | "
          + EventErrorCode.EVENT_NOT_FOUND_MESSAGE + " |\n";

  public static final String UPDATE_MD =
      "일정을 수정합니다.\n\n"
          + "- matchId, userId(인증), eventId로 권한과 존재를 함께 검증합니다.\n"
          + "- 기념일의 반복 설정은 변경할 수 없습니다.\n\n"
          + "### 에러 응답\n\n"
          + "| HTTP | errorCode | message |\n"
          + "|-----:|-----------|---------|\n"
          + "| " + HttpStatusCode.NOT_FOUND + " | " + EventErrorCode.EVENT_NOT_FOUND_ERROR_CODE + " | "
          + EventErrorCode.EVENT_NOT_FOUND_MESSAGE + " |\n"
          + "| " + HttpStatusCode.CONFLICT + " | " + EventErrorCode.EVENT_REPEAT_MODIFICATION_NOT_ALLOWED_ERROR_CODE + " | "
          + EventErrorCode.EVENT_REPEAT_MODIFICATION_NOT_ALLOWED_MESSAGE + " |\n";

  public static final String DELETE_MD =
      "일정을 수정합니다.\n\n"
          + "- matchId, userId(인증), eventId로 권한과 존재를 함께 검증합니다.\n"
          + "- 기념일은 삭제할 수 없습니다.\n\n"
          + "### 에러 응답\n\n"
          + "| HTTP | errorCode | message |\n"
          + "|-----:|-----------|---------|\n"
          + "| " + HttpStatusCode.NOT_FOUND + " | " + EventErrorCode.EVENT_NOT_FOUND_ERROR_CODE + " | "
          + EventErrorCode.EVENT_NOT_FOUND_MESSAGE + " |\n"
          + "| " + HttpStatusCode.CONFLICT + " | " + EventErrorCode.EVENT_DELETION_NOT_ALLOWED_ERROR_CODE + " | "
          + EventErrorCode.EVENT_DELETION_NOT_ALLOWED_MESSAGE + " |\n";
}
