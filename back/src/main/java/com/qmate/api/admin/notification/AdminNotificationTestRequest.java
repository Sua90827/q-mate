package com.qmate.api.admin.notification;

import com.qmate.domain.notification.entity.NotificationCategory;
import com.qmate.domain.notification.entity.NotificationCode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AdminNotificationTestRequest(
        @NotNull Long userId,
        @NotNull NotificationCategory category,
        @NotNull NotificationCode code,
        @NotBlank String pushTitle,
        @NotBlank String listTitle
) {}
