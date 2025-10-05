package com.qmate.common.constants.match;

import com.qmate.common.constants.HttpStatusCode;
import com.qmate.domain.user.User;
import com.qmate.exception.CommonErrorCode;
import com.qmate.exception.MatchErrorCode;
import com.qmate.exception.UserErrorCode;

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
  public static final String RESTORE_AGREED_AWAITING_PARTNER_MESSAGE = "복구에 동의했습니다. 상대방의 동의를 기다려주세요.";

  private static final String ERROR_RESPONSE_HEADER =

      "\n\n### 에러 응답\n\n"
          + "| HTTP | errorCode | message |\n"
          + "|-----:|-----------|---------|\n";

  // --- API Descriptions for Swagger ---

  public static final String CREATE_MATCH_MD =
      "사용자가 관계 유형(친구/연인)을 선택하여 새로운 매칭을 생성하고, 12시간 동안 유효한 초대 코드를 발급받습니다."
          + ERROR_RESPONSE_HEADER
          + "| " + HttpStatusCode.BAD_REQUEST + " | " + UserErrorCode.USER_NOT_FOUND_ERROR_CODE + " | " + UserErrorCode.USER_NOT_FOUND_MESSAGE + " |\n"
          + "| " + HttpStatusCode.BAD_REQUEST + " | " + MatchErrorCode.INVALID_START_DATE_FOR_COUPLE_ERROR_CODE + " | " + MatchErrorCode.INVALID_START_DATE_FOR_COUPLE_MESSAGE + " |\n"
          + "| " + HttpStatusCode.CONFLICT + " | " + MatchErrorCode.ALREADY_IN_MATCH_ERROR_CODE + " | " + MatchErrorCode.ALREADY_IN_MATCH_MESSAGE + " |\n";

  public static final String JOIN_MATCH_MD =
      "초대 코드를 사용하여 기존 매칭에 참여합니다. 매칭이 성사되면 `User`의 `current_match_id`가 업데이트되고, Redis의 초대 코드는 삭제됩니다."
          + ERROR_RESPONSE_HEADER
          + "| " + HttpStatusCode.BAD_REQUEST + " | " + UserErrorCode.USER_NOT_FOUND_ERROR_CODE + " | " + UserErrorCode.USER_NOT_FOUND_MESSAGE + " |\n"
          + "| " + HttpStatusCode.BAD_REQUEST + " | " + MatchErrorCode.INVITE_CODE_EXPIRED_ERROR_CODE + " | " + MatchErrorCode.INVITE_CODE_EXPIRED_MESSAGE + " |\n"
          + "| " + HttpStatusCode.BAD_REQUEST + " | " + MatchErrorCode.CANNOT_MATCH_WITH_SELF_ERROR_CODE + " | " + MatchErrorCode.CANNOT_MATCH_WITH_SELF_MESSAGE + " |\n"
          + "| " + HttpStatusCode.FORBIDDEN + " | " + MatchErrorCode.INVITE_ATTEMPT_LOCKED_ERROR_CODE + " | " +  MatchErrorCode.INVITE_ATTEMPT_LOCKED_MESSAGE + " |\n"
          + "| " + HttpStatusCode.NOT_FOUND + " | " + MatchErrorCode.MATCH_NOT_FOUND_ERROR_CODE + " | " + MatchErrorCode.MATCH_NOT_FOUND_MESSAGE + " |\n"
          + "| " + HttpStatusCode.CONFLICT + " | " + MatchErrorCode.ALREADY_IN_MATCH_ERROR_CODE + " | " + MatchErrorCode.ALREADY_IN_MATCH_MESSAGE + " |\n";

  public static final String GET_MATCH_INFO_MD =
      "특정 매칭의 상세 정보(관계 유형, 시작일, 상태, 멤버 정보 등)를 조회합니다."
          + ERROR_RESPONSE_HEADER
          + "| " + HttpStatusCode.NOT_FOUND + " | " + MatchErrorCode.MATCH_NOT_FOUND_ERROR_CODE + " | " + MatchErrorCode.MATCH_NOT_FOUND_MESSAGE + " |\n"
          + "| " + HttpStatusCode.FORBIDDEN + " | " + MatchErrorCode.MATCH_FORBIDDEN_ERROR_CODE + " | " + MatchErrorCode.MATCH_FORBIDDEN_MESSAGE + " |\n";

  public static final String GET_MATCH_MEMBERS_MD =
      "특정 매칭의 구성원 목록(상세 정보: 생년월일 포함)을 조회합니다."
          + ERROR_RESPONSE_HEADER
          + "| " + HttpStatusCode.NOT_FOUND + " | " + MatchErrorCode.MATCH_NOT_FOUND_ERROR_CODE + " | " + MatchErrorCode.MATCH_NOT_FOUND_MESSAGE + " |\n"
          + "| " + HttpStatusCode.FORBIDDEN + " | " + MatchErrorCode.MATCH_FORBIDDEN_ERROR_CODE + " | " + MatchErrorCode.MATCH_FORBIDDEN_MESSAGE + " |\n";

  public static final String UPDATE_MATCH_INFO_MD =
      "매칭의 속성(기념일, 질문 받는 시간)을 선택적으로 수정합니다."
          + ERROR_RESPONSE_HEADER
          + "| " + HttpStatusCode.NOT_FOUND + " | " + MatchErrorCode.MATCH_NOT_FOUND_ERROR_CODE + " | " + MatchErrorCode.MATCH_NOT_FOUND_MESSAGE + " |\n"
          + "| " + HttpStatusCode.FORBIDDEN + " | " + MatchErrorCode.MATCH_FORBIDDEN_ERROR_CODE + " | " + MatchErrorCode.MATCH_FORBIDDEN_MESSAGE + " |\n";

  public static final String DISCONNECT_MATCH_MD =
      "매칭 연결을 끊고, 2주간의 복구 유예 기간을 시작합니다."
          + ERROR_RESPONSE_HEADER
          + "| " + HttpStatusCode.NOT_FOUND + " | " + MatchErrorCode.MATCH_NOT_FOUND_ERROR_CODE + " | " + MatchErrorCode.MATCH_NOT_FOUND_MESSAGE + " |\n"
          + "| " + HttpStatusCode.FORBIDDEN + " | " + MatchErrorCode.MATCH_FORBIDDEN_ERROR_CODE + " | " + MatchErrorCode.MATCH_FORBIDDEN_MESSAGE + " |\n"
          + "| " + HttpStatusCode.CONFLICT + " | " + MatchErrorCode.MATCH_STATE_CONFLICT_ERROR_CODE + " | " + MatchErrorCode.MATCH_STATE_CONFLICT_MESSAGE + " |\n";

  public static final String RESTORE_MATCH_MD =
      "2주 유예 기간 내에 매칭을 복구합니다. 상호 동의가 필요합니다."
          + ERROR_RESPONSE_HEADER
          + "| " + HttpStatusCode.NOT_FOUND + " | " + MatchErrorCode.MATCH_NOT_FOUND_ERROR_CODE + " | " + MatchErrorCode.MATCH_NOT_FOUND_MESSAGE + " |\n"
          + "| " + HttpStatusCode.FORBIDDEN + " | " + MatchErrorCode.MATCH_FORBIDDEN_ERROR_CODE + " | " + MatchErrorCode.MATCH_FORBIDDEN_MESSAGE + " |\n"
          + "| " + HttpStatusCode.CONFLICT + " | " + MatchErrorCode.MATCH_STATE_CONFLICT_ERROR_CODE + " | " + MatchErrorCode.MATCH_STATE_CONFLICT_MESSAGE + " |\n"
          + "| " + HttpStatusCode.CONFLICT + " | " + MatchErrorCode.MATCH_RECOVERY_EXPIRED_ERROR_CODE + " | " + MatchErrorCode.MATCH_RECOVERY_EXPIRED_MESSAGE + " |\n";

  public static final String VALIDATE_INVITE_CODE_MD =
      "사용자가 매칭에 참여하기 전에, 초대 코드의 유효성과 상대방의 닉네임을 미리 확인합니다."
          + ERROR_RESPONSE_HEADER
          + "| " + HttpStatusCode.BAD_REQUEST + " | " + MatchErrorCode.INVITE_CODE_EXPIRED_ERROR_CODE + " | " + MatchErrorCode.INVITE_CODE_EXPIRED_MESSAGE + " |\n"
          + "| " + HttpStatusCode.NOT_FOUND + " | " + MatchErrorCode.MATCH_NOT_FOUND_ERROR_CODE + " | " + MatchErrorCode.MATCH_NOT_FOUND_MESSAGE + " |\n"
          + "| " + HttpStatusCode.INTERNAL_SERVER_ERROR + " | " + CommonErrorCode.INTERNAL_SERVER_ERROR_CODE + " | " + CommonErrorCode.INTERNAL_SERVER_ERROR_CODE + " |\n";

  public static final String GET_LOCK_STATUS_MD =
      "초대 코드 입력 5회 실패로 계정이 잠겼을 경우, 남은 잠금 시간을 조회합니다."
          + ERROR_RESPONSE_HEADER
          + "| " + HttpStatusCode.UNAUTHORIZED + " | " + CommonErrorCode.UNAUTHORIZED_ERROR_CODE + " | " + CommonErrorCode.UNAUTHORIZED_MESSAGE + " |\n";

  public static final String GET_DETACHED_STATUS_MD =
      "로그인한 사용자가 복구 가능한 '연결 끊김' 상태의 매칭이 있는지 조회합니다."
          + ERROR_RESPONSE_HEADER
          + "| " + HttpStatusCode.UNAUTHORIZED + " | " + CommonErrorCode.UNAUTHORIZED_ERROR_CODE + " | " + CommonErrorCode.UNAUTHORIZED_MESSAGE + " |\n";
}
