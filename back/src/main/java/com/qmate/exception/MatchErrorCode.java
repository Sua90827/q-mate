package com.qmate.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class MatchErrorCode extends ErrorCode {

  //에러 메시지 상수화
  private static final String ALREADY_IN_MATCH_MESSAGE = "이미 다른 매칭에 참여 중이거나, 해당 매칭에 참여할 수 없습니다.";
  private static final String MATCH_NOT_FOUND_MESSAGE = "해당 매칭을 찾을 수 없습니다.";
  private static final String PARTNER_NOT_FOUND_MESSAGE = "파트너 정보를 찾을 수 없습니다.";
  private static final String INVITE_CODE_EXPIRED_MESSAGE = "초대 코드가 만료되었거나 유효하지 않습니다.";
  private static final String INVALID_START_DATE_FOR_COUPLE_MESSAGE = "연인 관계는 기념일(YYYY-MM-DD)을 필수로 입력해야 합니다.";
  private static final String INVITE_ATTEMPT_LOCKED_MESSAGE = "초대 코드 입력 5회 실패하여 24시간 동안 시도할 수 없습니다.";
  private static final String CANNOT_MATCH_WITH_SELF_MESSAGE = "자기 자신과 매칭할 수 없습니다.";
  // 에러 코드 객체 반환 메서드
  public static ErrorCode alreadyInMatch() {
    return new MatchErrorCode(HttpStatus.CONFLICT, "MATCH_001", ALREADY_IN_MATCH_MESSAGE);
  }

  public static ErrorCode matchNotFound() {
    return new MatchErrorCode(HttpStatus.NOT_FOUND, "MATCH_002", MATCH_NOT_FOUND_MESSAGE);
  }

  public static ErrorCode partnerNotFound() {
    return new MatchErrorCode(HttpStatus.NOT_FOUND, "MATCH_003", PARTNER_NOT_FOUND_MESSAGE);
  }

  public static ErrorCode inviteCodeExpired() {
    return new MatchErrorCode(HttpStatus.BAD_REQUEST, "MATCH_004", INVITE_CODE_EXPIRED_MESSAGE);
  }

  public static ErrorCode invalidStartDateForCouple() {
    return new MatchErrorCode(HttpStatus.BAD_REQUEST, "MATCH_005",
        INVALID_START_DATE_FOR_COUPLE_MESSAGE);
  }
  public static ErrorCode inviteAttemptLocked(){
    return new MatchErrorCode(HttpStatus.FORBIDDEN, "MATCH_007",INVITE_ATTEMPT_LOCKED_MESSAGE);
  }

  public static ErrorCode cannotMatchWithSelf() {
    return new MatchErrorCode(HttpStatus.BAD_REQUEST, "MATCH_006", CANNOT_MATCH_WITH_SELF_MESSAGE);
  }

  private MatchErrorCode(HttpStatus httpStatus, String code, String message) {
    super(httpStatus, code, message);
  }


}
