package com.qmate.domain.event.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.qmate.domain.event.entity.EventAlarmOption;
import com.qmate.domain.event.entity.EventRepeatType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class EventResponse {

  private final Long eventId;
  private final String title;
  private final String description;
  private final LocalDate eventAt;
  private final EventRepeatType repeatType;
  private final EventAlarmOption alarmOption;

  @JsonProperty("isAnniversary")
  private final boolean anniversary;

  private final LocalDateTime createdAt;
  private final LocalDateTime updatedAt;
}
