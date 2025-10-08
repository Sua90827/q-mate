package com.qmate.common.constants.notification;

import com.qmate.common.constants.HttpStatusCode;
import com.qmate.exception.errorcode.NotificationErrorCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class NotificationConstants {

  // code descriptions
  public static final String QI_TODAY_READY_MSG = "오늘의 질문 도착!";
  public static final String QI_REMINDER_MSG = "아직 답변하지 않은 질문이 있어요.";
  public static final String QI_COMPLETED_MSG = "상대의 답변 도착!";
  public static final String EVENT_SAME_DAY_MSG = "[당일 일정 알림]";
  public static final String EVENT_THREE_DAYS_BEFORE_MSG = "[3일 전 일정 알림]";
  public static final String EVENT_WEEK_BEFORE_MSG = "[1주일 전 일정 알림]";
  public static final String MATCH_COMPLETED_MSG = "매칭이 성사되었습니다.";

  // api docs
  public static final String GET_DETAIL_MD =
      "특정 알림의 상세 정보를 조회합니다.\n\n"
          + "이때, 해당 알림이 읽지 않은 상태였다면 읽음 처리됩니다.\n\n"
          + "### 에러 응답\n\n"
          + "| HTTP | errorCode | message |\n"
          + "|-----:|-----------|---------|\n"
          + "| " + HttpStatusCode.NOT_FOUND + " | " + NotificationErrorCode.NOTIFICATION_NOT_FOUND_ERROR_CODE + " | "
          + NotificationErrorCode.NOTIFICATION_NOT_FOUND_MESSAGE + " |\n\n"
          + "## 알림 코드\n\n"
          + "### 질문(QI)\n"
          + "- QI_TODAY_READY: " + QI_TODAY_READY_MSG + "\n"
          + "- QI_REMINDER: " + QI_REMINDER_MSG + "\n"
          + "- QI_COMPLETED: " + QI_COMPLETED_MSG + "\n\n"
          + "### 일정(Event)\n"
          + "- EVENT_SAME_DAY: " + EVENT_SAME_DAY_MSG + "\n"
          + "- EVENT_THREE_DAY_BEFORE: " + EVENT_THREE_DAYS_BEFORE_MSG + "\n"
          + "- EVENT_WEEK_BEFORE: " + EVENT_WEEK_BEFORE_MSG + "\n";

  public static final String GET_LIST_MD =
      "알림 목록을 조회합니다.\n\n"
          + "- 필터: `category`(옵션), `code`(옵션), `unread`(옵션; true이면 미읽음만)\n"
          + "- 페이징: `page`(0..N), `size`(기본 20)\n"
          + "- 정렬: 생성시각 내림차순 고정(요청의 `sort`는 무시)";

  public static final String GET_UNREAD_COUNT_MD =
      "읽지 않은 알림의 개수를 조회합니다.\n\n";

  public static final String DELETE_MD =
      "특정 알림을 삭제합니다.\n\n"
          + "### 에러 응답\n\n"
          + "| HTTP | errorCode | message |\n"
          + "|-----:|-----------|---------|\n"
          + "| " + HttpStatusCode.NOT_FOUND + " | " + NotificationErrorCode.NOTIFICATION_NOT_FOUND_ERROR_CODE + " | "
          + NotificationErrorCode.NOTIFICATION_NOT_FOUND_MESSAGE + " |\n\n";
}
