package com.qmate.domain.event.mapper;

import com.qmate.domain.event.entity.Event;
import com.qmate.domain.event.model.response.EventResponse;
import java.util.Objects;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class EventMapper {

  public static EventResponse toResponse(Event e) {
    if (Objects.isNull(e)) {
      return null;
    }

    return EventResponse.builder()
        .eventId(e.getId())
        .title(e.getTitle())
        .description(e.getDescription())
        .eventAt(e.getEventAt())
        .repeatType(e.getRepeatType())
        .alarmOption(e.getAlarmOption())
        .anniversary(e.isAnniversary())
        .createdAt(e.getCreatedAt())
        .updatedAt(e.getUpdatedAt())
        .build();
  }
}
