package com.qmate.domain.event.model.request;

import com.qmate.domain.event.entity.EventAlarmOption;
import com.qmate.domain.event.entity.EventRepeatType;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class EventCreateRequest {

  @NotBlank
  @Size(max = 120)
  private String title;

  @Nullable
  @Size(max = 1000)
  private String description; // nullable 허용

  @NotNull
  private LocalDate eventAt; // YYYY-MM-DD

  @NotNull
  private EventRepeatType repeatType;  // NONE | WEEKLY | MONTHLY | YEARLY

  @NotNull
  private EventAlarmOption alarmOption; // NONE | WEEK_BEFORE | THREE_DAYS_BEFORE | SAME_DAY
}
