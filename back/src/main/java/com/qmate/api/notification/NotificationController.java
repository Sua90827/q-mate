package com.qmate.api.notification;

import com.qmate.common.constants.notification.NotificationConstants;
import com.qmate.domain.notification.entity.NotificationCategory;
import com.qmate.domain.notification.entity.NotificationCode;
import com.qmate.domain.notification.model.response.NotificationListItem;
import com.qmate.domain.notification.model.response.NotificationResponse;
import com.qmate.domain.notification.service.NotificationService;
import com.qmate.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Notification", description = "알림 API")
public class NotificationController {

  private final NotificationService notificationService;

  @Operation(
      summary = "알림 상세 조회",
      description = NotificationConstants.GET_DETAIL_MD,
      parameters = {
          @Parameter(name = "notificationId", description = "알림 ID")
      }
  )
  @GetMapping("/{notificationId}")
  public NotificationResponse getDetail(
      @AuthenticationPrincipal UserPrincipal principal,
      @PathVariable Long notificationId
  ) {
    return notificationService.getDetail(principal.userId(), notificationId);
  }

  @Operation(
      summary = "알림 리스트 조회",
      description = NotificationConstants.GET_LIST_MD,
      parameters = {
          @Parameter(name = "category", description = "알림 카테고리 (optional)", schema = @Schema(implementation = NotificationCategory.class)),
          @Parameter(name = "code", description = "알림 코드 (optional)", schema = @Schema(implementation = NotificationCode.class)),
          @Parameter(name = "unread", description = "읽지 않은 알림만 조회 여부 (optional)"),
          @Parameter(name = "page", description = "페이지 번호 (0..N)"),
          @Parameter(name = "size", description = "페이지 크기"),
          @Parameter(name = "sort", description = "사용하지 않음")
      }
  )
  @GetMapping
  public Page<NotificationListItem> getList(
      @AuthenticationPrincipal UserPrincipal principal,
      @RequestParam(required = false) NotificationCategory category,
      @RequestParam(required = false) NotificationCode code,
      @RequestParam(required = false) Boolean unread,
      @PageableDefault(page = 0, size = 20)
      @ParameterObject Pageable pageable
  ) {
    return notificationService.getList(principal.userId(), category, code, unread, pageable);
  }

  @Operation(
      summary = "읽지 않은 알림 개수 조회",
      description = NotificationConstants.GET_UNREAD_COUNT_MD
  )
  @GetMapping("/unread-count")
  public long getUnreadCount(@AuthenticationPrincipal UserPrincipal principal) {
    return notificationService.getUnreadCount(principal.userId());
  }

  @Operation(summary = "알림 삭제", description = NotificationConstants.DELETE_MD)
  @DeleteMapping("/{notificationId}")
  @ResponseStatus(HttpStatus.NO_CONTENT) // 204
  public ResponseEntity<Void> delete(
      @AuthenticationPrincipal UserPrincipal principal,
      @PathVariable long notificationId
  ) {
    notificationService.deleteAuthorized(principal.userId(), notificationId);
    return ResponseEntity.noContent().build();
  }
}
