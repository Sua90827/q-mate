package com.qmate.domain.notification.model.response;

import com.qmate.domain.notification.entity.PushSubscription;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PushSubscriptionRegisterResponse {
  private Long subscriptionId;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public static PushSubscriptionRegisterResponse from(PushSubscription entity) {
    return new PushSubscriptionRegisterResponse(
        entity.getId(),
        entity.getCreatedAt(),
        entity.getUpdatedAt()
    );
  }
}
