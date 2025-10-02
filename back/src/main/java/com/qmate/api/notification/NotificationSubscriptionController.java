package com.qmate.api.notification;

import com.qmate.domain.notification.model.request.PushSubscriptionRegisterRequest;
import com.qmate.domain.notification.model.response.PushSubscriptionRegisterResponse;
import com.qmate.domain.notification.model.response.VapidPublicKeyResponse;
import com.qmate.domain.notification.service.PushSubscriptionService;
import com.qmate.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notifications/subscriptions")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Notification Subscription", description = "푸시 알림 구독 관련 API")
public class NotificationSubscriptionController {

  private final PushSubscriptionService pushSubscriptionService;

  @Value("${webpush.vapid.public-key}")
  private String vapidPublicKey;

  @Operation(
      summary = "푸시 알림 구독/갱신",
      description = """
          브라우저의 PushSubscription 객체를 **그대로 본문에 담아** 보내면 서버가 `endpoint`와 `keys.p256dh/auth`를 추출해
          구독을 생성하거나 갱신합니다. 클라이언트에서 별도 가공이나 해시 계산은 필요 없습니다.
          
          - 호출 시점: 로그인 직후 또는 알림 설정을 켤 때
          - 동작: 기존 구독이 있으면 소유권을 현재 사용자로 이전하고 키를 최신화, 없으면 새로 생성
          """
  )
  @PostMapping
  public ResponseEntity<PushSubscriptionRegisterResponse> upsert(
      @AuthenticationPrincipal UserPrincipal me,
      @Valid @RequestBody PushSubscriptionRegisterRequest req) {

    PushSubscriptionRegisterResponse resp = pushSubscriptionService.upsert(me.userId(), req);
    return ResponseEntity.ok(resp);
  }

  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(
      summary = "푸시 알림 구독 해지 (엔드포인트 기준)",
      description = """
          브라우저에서 `getSubscription()`으로 얻은 **`subscription.endpoint` 값을 그대로** `endpoint` 쿼리로 보내면,
          서버가 해시를 계산해 현재 사용자 소유의 구독만 안전하게 삭제합니다.
          별도의 `subscriptionId` 보관은 필요 없습니다.
          
          - 호출 시점: 로그아웃 직전/직후 훅에서 호출 권장
          """
  )
  @DeleteMapping("/by-endpoint")
  public ResponseEntity<Void> unsubscribeByEndpoint(
      @AuthenticationPrincipal UserPrincipal me,
      @RequestParam("endpoint") String endpoint) {

    pushSubscriptionService.unsubscribe(me.userId(), endpoint);
    return ResponseEntity.noContent().build();
  }

  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(
      summary = "푸시 알림 구독 해지 (구독 ID 기준)",
      description = """
          이미 발급받은 `subscriptionId`를 보관하고 있다면 경로 변수로 전달해 삭제할 수 있습니다.
          """
  )
  @DeleteMapping("/{subscriptionId}")
  public ResponseEntity<Void> unsubscribeById(
      @AuthenticationPrincipal UserPrincipal me,
      @PathVariable Long subscriptionId) {
    pushSubscriptionService.unsubscribe(me.userId(), subscriptionId);
    return ResponseEntity.noContent().build();
  }

  @Operation(summary = "VAPID 공개키 조회",
      description = "브라우저 Push API 구독에 사용할 VAPID 공개키를 반환합니다. 클라이언트에서 그대로 쓰면 됩니다.")
  @GetMapping("/vapid-public-key")
  public ResponseEntity<VapidPublicKeyResponse> getVapidPublicKey() {
    return ResponseEntity.ok(VapidPublicKeyResponse.builder()
        .vapidPublicKey(vapidPublicKey)
        .build());
  }
}
