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
public class EventUpdateRequest {

  @Nullable
  @Size(max = EventConstants.EVENT_TITLE_MAX_LENGTH, message = EventConstants.EVENT_TITLE_SIZE_MESSAGE)
  private String title;            // null이면 변경 없음

  @Nullable
  @Size(max = EventConstants.EVENT_DESCRIPTION_MAX_LENGTH, message = EventConstants.EVENT_DESCRIPTION_SIZE_MESSAGE)
  private String description;      // null이면 변경 없음 (비우기 불가)

  @Nullable
  private LocalDate eventAt;       // null이면 변경 없음

  @Nullable
  private EventRepeatType repeatType;   // null이면 변경 없음

  @Nullable
  private EventAlarmOption alarmOption; // null이면 변경 없음
}
