package com.qmate.common.constants.event;

import com.qmate.common.constants.HttpStatusCode;
import com.qmate.exception.CommonErrorCode;
import com.qmate.exception.MatchErrorCode;
import com.qmate.exception.errorcode.CustomQuestionErrorCode;
import com.qmate.exception.errorcode.EventErrorCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class EventConstants {

  // 상수
  public static final int EVENT_TITLE_MAX_LENGTH = 120;
  public static final int EVENT_DESCRIPTION_MAX_LENGTH = 1000;
  // 이벤트 리스트 조회 기간 최대 범위
  public static final int EVENT_LIST_MAX_RANGE_YEARS = 3;
  // 일정 캘린더 조회 최대 범위
  public static final int EVENT_CALENDAR_MAX_RANGE_DAYS = 60;

  // validation message
  // 제목 공백 불가
  public static final String EVENT_TITLE_NOT_BLANK_MESSAGE = "일정 제목은 필수 입력 값입니다.";
  public static final String EVENT_TITLE_SIZE_MESSAGE = "일정 제목은 최대 " + EVENT_TITLE_MAX_LENGTH + "자까지 가능합니다.";
  public static final String EVENT_DESCRIPTION_SIZE_MESSAGE = "일정 설명은 최대 " + EVENT_DESCRIPTION_MAX_LENGTH + "자까지 가능합니다.";

  // api doc
  public static final String CREATE_MD =
      "매치 하위에 일정을 생성합니다.\n\n"
          + "- matchId, userId(인증)로 권한과 존재를 함께 검증합니다.\n\n"
          + "### 에러 응답\n\n"
          + "| HTTP | errorCode | message |\n"
          + "|-----:|-----------|---------|\n"
          + "| " + HttpStatusCode.NOT_FOUND + " | " + MatchErrorCode.MATCH_NOT_FOUND_ERROR_CODE + " | "
          + MatchErrorCode.MATCH_NOT_FOUND_MESSAGE + " |\n";

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

  public static final String LIST_MD =
      "지정한 기간[from, to]에 대해 매치의 일정들을 조회합니다.\n\n"
          + "#### 규칙\n"
          + "- from, to **모두 포함**입니다. (예: from==발생일, to==발생일도 결과에 포함)\n"
          + "- 페이징: `page`(0-based), `size`(기본 20, **최대 100**; 초과 시 100으로 캡).\n"
          + "- 필터: `repeatType`(NONE|WEEKLY|MONTHLY|YEARLY), `anniversary`(true/false). 미지정 시 전체 대상.\n"
          + "- 정렬: **발생일 오름차순**, 동일일자는 **eventId 오름차순**.\n"
          + "- 권한: `user.currentMatchId == {matchId}` 인 경우에만 조회되며, 미충족 시 **빈 페이지**가 반환됩니다.\n"
          + "- 날짜 형식: `YYYY-MM-DD`.\n\n"
          + "#### 반복 전개\n"
          + "- WEEKLY: seed 요일 기준 주 단위 전개.\n"
          + "- MONTHLY: seed의 일(day) 기준 전개, 말일 초과 시 해당 월의 **말일로 보정**.\n"
          + "- YEARLY: seed의 월/일 그대로 전개, **2/29는 평년에는 2/28로 보정**.\n\n"
          + "### 에러 응답\n\n"
          + "| HTTP | errorCode | message |\n"
          + "|-----:|-----------|---------|\n"
          + "| " + HttpStatusCode.BAD_REQUEST + " | " + EventErrorCode.EVENT_LIST_DATE_RANGE_EXCEEDED_ERROR_CODE + " | "
          + EventErrorCode.EVENT_LIST_DATE_RANGE_EXCEEDED_MESSAGE + " |\n";

  public static final String CALENDAR_MD =
      "지정한 기간[from, to]에 대해 매치의 캘린더 이벤트를 조회합니다.\n\n"
          + "#### 규칙\n"
          + "- from, to **모두 포함**입니다. (예: from==발생일, to==발생일도 결과에 포함)\n"
          + "- 조회 기간은 **최대 60일**(포함 기준)까지만 허용합니다. **같은 달 제한은 없습니다.**\n"
          + "- **페이징 없음**: 캘린더 특성상 날짜당 최대 1개만 반환됩니다.\n"
          + "- 집계 규칙(같은 날짜에 여러 이벤트 존재 시):\n"
          + "  - **대표 eventId = 최솟값**\n"
          + "  - **isAnniversary = OR** (해당 날짜에 하나라도 기념일이 있으면 true)\n"
          + "- 정렬: **날짜(eventAt) 오름차순**.\n"
          + "- 권한: `user.currentMatchId == {matchId}` 인 경우에만 조회되며, 미충족 시 **빈 결과**가 반환됩니다.\n"
          + "- 날짜 형식: `YYYY-MM-DD`.\n\n"
          + "#### 반복 전개\n"
          + "- WEEKLY: seed 요일 기준 주 단위 전개.\n"
          + "- MONTHLY: seed의 일(day) 기준 전개, 말일 초과 시 해당 월의 **말일로 보정**.\n"
          + "- YEARLY: seed의 월/일 그대로 전개, **2/29는 평년에는 2/28로 보정**.\n\n"
          + "### 에러 응답\n\n"
          + "| HTTP | errorCode | message |\n"
          + "|-----:|-----------|---------|\n"
          + "| " + HttpStatusCode.BAD_REQUEST + " | " + EventErrorCode.EVENT_CALENDAR_DATE_RANGE_EXCEEDED_ERROR_CODE + " | "
          + EventErrorCode.EVENT_CALENDAR_DATE_RANGE_EXCEEDED_MESSAGE + " |\n";
}
