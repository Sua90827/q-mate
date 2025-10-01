package com.qmate.domain.notification.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PushSettingUpdateRequest {
  @NotNull
  private Boolean pushEnabled;
}
