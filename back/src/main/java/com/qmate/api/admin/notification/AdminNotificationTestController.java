package com.qmate.api.admin.notification;

import com.qmate.common.push.PushSender;
import com.qmate.domain.notification.entity.Notification;
import com.qmate.domain.notification.entity.NotificationResourceType;
import com.qmate.domain.notification.repository.NotificationRepository;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.validation.Valid;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/admin/notifications")
@RequiredArgsConstructor
@Hidden
public class AdminNotificationTestController {

    private final NotificationRepository notificationRepository;
    private final PushSender pushSender;

    @PostMapping("/test")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Object> createAndSend(@Valid @RequestBody AdminNotificationTestRequest req) {
        log.warn("AdminNotificationTestController.createAndSend: userId={}, category={}, code={}",
                req.userId(), req.category(), req.code());
        Notification notification = Notification.builder()
                .userId(req.userId())
                .category(req.category())
                .code(req.code())
                .pushTitle(req.pushTitle())
                .listTitle(req.listTitle())
                .resourceType(NotificationResourceType.NONE)
                .build();

        Notification saved = notificationRepository.save(notification);
        // 호출 측에서 푸시 설정 ON이라고 가정 → 바로 전송
        pushSender.send(saved);

        return Map.of("notificationId", saved.getId());
    }
}
