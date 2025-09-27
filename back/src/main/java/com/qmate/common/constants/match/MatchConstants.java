package com.qmate.common.constants.match;

public class MatchConstants {

  // MatchUpdateRequest
  public static final String HOUR_MIN_MESSAGE = "질문 시간은 0 이상이어야 합니다.";
  public static final String HOUR_MAX_MESSAGE = "질문 시간은 23 이하여야 합니다.";

  // MatchJoinRequest
  public static final String INVITE_CODE_NOT_BLANK = "초대 코드를 입력해주세요.";
  public static final String INVITE_CODE_SIZE = "초대 코드는 6자리 숫자입니다.";

  // MatchCreationRequest
  public static final String RELATION_TYPE_NOT_NULL = "관계 유형을 선택해주세요.";
  public static final String VALID_START_DATE_DEFAULT = "연인(COUPLE) 관계는 기념일(startDate)을 필수로 입력해야 합니다.";

  // MatchController
  public static final String DISCONNECT_SUCCESS_MESSAGE = "매칭 연결 끊기 요청이 처리되었습니다. 2주 후에 데이터가 삭제됩니다.";
  public static final String RESTORE_SUCCESS_MESSAGE = "매칭이 성공적으로 복구되었습니다.";
}
