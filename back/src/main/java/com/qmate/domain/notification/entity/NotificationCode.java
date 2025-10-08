package com.qmate.domain.notification.entity;

import com.qmate.common.constants.notification.NotificationConstants;
import lombok.Getter;

@Getter
public enum NotificationCode {
  // QUESTION (QI)
  QI_TODAY_READY(NotificationConstants.QI_TODAY_READY_MSG),
  QI_REMINDER(NotificationConstants.QI_REMINDER_MSG),
  QI_COMPLETED(NotificationConstants.QI_COMPLETED_MSG),

  // EVENT
  EVENT_SAME_DAY(NotificationConstants.EVENT_SAME_DAY_MSG),
  EVENT_THREE_DAYS_BEFORE(NotificationConstants.EVENT_THREE_DAYS_BEFORE_MSG),
  EVENT_WEEK_BEFORE(NotificationConstants.EVENT_WEEK_BEFORE_MSG),

  // MATCH
  MATCH_COMPLETED(NotificationConstants.MATCH_COMPLETED_MSG),;


  private final String description;

  NotificationCode(String description) {
    this.description = description;
  }
}
