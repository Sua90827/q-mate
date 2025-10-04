package com.qmate.domain.notification.model.response;

import com.qmate.domain.notification.entity.Notification;
import com.qmate.domain.notification.entity.NotificationCategory;
import com.qmate.domain.notification.entity.NotificationCode;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NotificationListItem {
  private final Long notificationId;
  private final NotificationCategory category;
  private final NotificationCode code;
  private final String listTitle;
  private final LocalDateTime createdAt;
  private final boolean read;

  public static NotificationListItem from(Notification n) {
    return NotificationListItem.builder()
        .notificationId(n.getId())
        .category(n.getCategory())
        .code(n.getCode())
        .listTitle(n.getListTitle())
        .createdAt(n.getCreatedAt())
        .read(n.getReadAt() != null)
        .build();
  }
}
