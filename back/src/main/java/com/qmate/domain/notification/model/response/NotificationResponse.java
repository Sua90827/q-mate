package com.qmate.domain.notification.model.response;

import com.qmate.domain.notification.entity.Notification;
import com.qmate.domain.notification.entity.NotificationCategory;
import com.qmate.domain.notification.entity.NotificationCode;
import com.qmate.domain.notification.entity.NotificationResourceType;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NotificationResponse {
  private final Long notificationId;
  private final Long userId;
  private final Long matchId;
  private final NotificationCategory category;
  private final NotificationCode code;
  private final String listTitle;
  private final String pushTitle;
  private final NotificationResourceType resourceType;
  private final Long resourceId;
  private final LocalDateTime readAt;
  private final LocalDateTime createdAt;

  public static NotificationResponse from(Notification n) {
    return NotificationResponse.builder()
        .notificationId(n.getId())
        .userId(n.getUserId())
        .matchId(n.getMatchId())
        .category(n.getCategory())
        .code(n.getCode())
        .listTitle(n.getListTitle())
        .pushTitle(n.getPushTitle())
        .resourceType(n.getResourceType())
        .resourceId(n.getResourceId())
        .readAt(n.getReadAt())
        .createdAt(n.getCreatedAt())
        .build();
  }
}
