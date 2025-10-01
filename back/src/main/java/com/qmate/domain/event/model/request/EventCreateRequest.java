package com.qmate.domain.event.model.request;

import com.qmate.common.constants.event.EventConstants;
import com.qmate.domain.event.entity.EventAlarmOption;
import com.qmate.domain.event.entity.EventRepeatType;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventCreateRequest {

  @NotBlank(message = EventConstants.EVENT_TITLE_NOT_BLANK_MESSAGE)
  @Size(max = EventConstants.EVENT_TITLE_MAX_LENGTH, message = EventConstants.EVENT_TITLE_SIZE_MESSAGE)
  private String title;

  @Nullable
  @Size(max = EventConstants.EVENT_DESCRIPTION_MAX_LENGTH, message = EventConstants.EVENT_DESCRIPTION_SIZE_MESSAGE)
  private String description; // nullable 허용

  @NotNull
  private LocalDate eventAt; // YYYY-MM-DD

  @NotNull
  private EventRepeatType repeatType;  // NONE | WEEKLY | MONTHLY | YEARLY

  @NotNull
  private EventAlarmOption alarmOption; // NONE | WEEK_BEFORE | THREE_DAYS_BEFORE | SAME_DAY
}
