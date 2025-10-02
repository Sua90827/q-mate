package com.qmate.api.notification;

import com.qmate.common.constants.notification.PushConstants;
import com.qmate.domain.notification.model.request.PushSettingUpdateRequest;
import com.qmate.domain.notification.model.response.PushSettingResponse;
import com.qmate.domain.notification.service.PushSettingService;
import com.qmate.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notifications/settings")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Notification Setting", description = "알림 설정 관련 API")
public class NotificationSettingController {

  private final PushSettingService pushSettingService;

  @Operation(summary = "알림 설정 조회", description = "사용자의 현재 알림 설정을 조회합니다.")
  @GetMapping
  public ResponseEntity<PushSettingResponse> get(
      @AuthenticationPrincipal UserPrincipal principal) {
    PushSettingResponse body = pushSettingService.get(principal.userId());
    return ResponseEntity.ok(body);
  }

  @Operation(
      summary = "알림 설정 수정",
      description = PushConstants.UPDATE_PUSH_SETTING_MD
  )
  @PatchMapping
  public ResponseEntity<PushSettingResponse> update(
      @AuthenticationPrincipal UserPrincipal principal,
      @Valid @RequestBody PushSettingUpdateRequest request) {
    PushSettingResponse body = pushSettingService.update(principal.userId(), request.getPushEnabled());
    return ResponseEntity.ok(body);
  }
}
