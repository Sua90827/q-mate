package com.qmate.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class MatchErrorCode extends ErrorCode {

  //에러 메시지 상수화
  public static final String ALREADY_IN_MATCH_MESSAGE = "이미 다른 매칭에 참여 중이거나, 해당 매칭에 참여할 수 없습니다.";
  public static final String MATCH_NOT_FOUND_MESSAGE = "해당 매칭을 찾을 수 없습니다.";
  public static final String PARTNER_NOT_FOUND_MESSAGE = "파트너 정보를 찾을 수 없습니다.";
  public static final String INVITE_CODE_EXPIRED_MESSAGE = "초대 코드가 만료되었거나 유효하지 않습니다.";
  public static final String INVALID_START_DATE_FOR_COUPLE_MESSAGE = "연인 관계는 기념일(YYYY-MM-DD)을 필수로 입력해야 합니다.";
  public static final String INVITE_ATTEMPT_LOCKED_MESSAGE = "초대 코드 입력 5회 실패하여 24시간 동안 시도할 수 없습니다.";
  public static final String CANNOT_MATCH_WITH_SELF_MESSAGE = "자기 자신과 매칭할 수 없습니다.";
  public static final String MATCH_FORBIDDEN_MESSAGE = "해당 매칭에 대한 접근 권한이 없습니다.";
  public static final String MATCH_STATE_CONFLICT_MESSAGE = "요청을 처리할 수 없는 매칭 상태입니다.";
  public static final String MATCH_RECOVERY_EXPIRED_MESSAGE = "복구 가능 기간(2주)이 지나 매칭을 복구할 수 없습니다.";

  //에러 코드 상수
  public static final String ALREADY_IN_MATCH_ERROR_CODE = "MATCH_001";
  public static final String MATCH_NOT_FOUND_ERROR_CODE = "MATCH_002";
  public static final String PARTNER_NOT_FOUND_ERROR_CODE = "MATCH_003";
  public static final String INVITE_CODE_EXPIRED_ERROR_CODE = "MATCH_004";
  public static final String INVALID_START_DATE_FOR_COUPLE_ERROR_CODE = "MATCH_005";
  public static final String CANNOT_MATCH_WITH_SELF_ERROR_CODE = "MATCH_006";
  public static final String INVITE_ATTEMPT_LOCKED_ERROR_CODE = "MATCH_007";
  public static final String MATCH_FORBIDDEN_ERROR_CODE = "MATCH_008";
  public static final String MATCH_STATE_CONFLICT_ERROR_CODE = "MATCH_009";
  public static final String MATCH_RECOVERY_EXPIRED_ERROR_CODE = "MATCH_010";
  // 에러 코드 객체 반환 메서드
  public static ErrorCode alreadyInMatch() {
    return new MatchErrorCode(HttpStatus.CONFLICT, ALREADY_IN_MATCH_ERROR_CODE, ALREADY_IN_MATCH_MESSAGE);
  }

  public static ErrorCode matchNotFound() {
    return new MatchErrorCode(HttpStatus.NOT_FOUND, MATCH_NOT_FOUND_ERROR_CODE, MATCH_NOT_FOUND_MESSAGE);
  }

  public static ErrorCode partnerNotFound() {
    return new MatchErrorCode(HttpStatus.NOT_FOUND, PARTNER_NOT_FOUND_ERROR_CODE, PARTNER_NOT_FOUND_MESSAGE);
  }

  public static ErrorCode inviteCodeExpired() {
    return new MatchErrorCode(HttpStatus.BAD_REQUEST, INVITE_CODE_EXPIRED_ERROR_CODE, INVITE_CODE_EXPIRED_MESSAGE);
  }

  public static ErrorCode invalidStartDateForCouple() {
    return new MatchErrorCode(HttpStatus.BAD_REQUEST, INVALID_START_DATE_FOR_COUPLE_ERROR_CODE,
        INVALID_START_DATE_FOR_COUPLE_MESSAGE);
  }

  public static ErrorCode cannotMatchWithSelf() {
    return new MatchErrorCode(HttpStatus.BAD_REQUEST, CANNOT_MATCH_WITH_SELF_ERROR_CODE, CANNOT_MATCH_WITH_SELF_MESSAGE);
  }
  public static ErrorCode inviteAttemptLocked(){
    return new MatchErrorCode(HttpStatus.FORBIDDEN, INVITE_ATTEMPT_LOCKED_ERROR_CODE,INVITE_ATTEMPT_LOCKED_MESSAGE);
  }


  public static ErrorCode matchForbidden(){
    return new MatchErrorCode(HttpStatus.FORBIDDEN, MATCH_FORBIDDEN_ERROR_CODE, MATCH_FORBIDDEN_MESSAGE);
  }

  public static ErrorCode matchStateConflict(){
    return new MatchErrorCode(HttpStatus.CONFLICT, MATCH_STATE_CONFLICT_ERROR_CODE,MATCH_STATE_CONFLICT_MESSAGE);
  }

  public static ErrorCode matchRecoveryExpired() {
    return new MatchErrorCode(HttpStatus.CONFLICT, MATCH_RECOVERY_EXPIRED_ERROR_CODE, MATCH_RECOVERY_EXPIRED_MESSAGE);
  }

  private MatchErrorCode(HttpStatus httpStatus, String code, String message) {
    super(httpStatus, code, message);
  }


}
