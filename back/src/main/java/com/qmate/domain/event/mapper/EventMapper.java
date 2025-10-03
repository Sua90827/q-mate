package com.qmate.domain.event.mapper;

import com.qmate.domain.event.entity.Event;
import com.qmate.domain.event.model.request.EventCreateRequest;
import com.qmate.domain.event.model.response.EventResponse;
import com.qmate.domain.match.Match;
import java.time.LocalDate;
import java.util.Objects;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class EventMapper {

  /**
   * Event Entity -> Event Response
   */
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

  /**
   * Event Create Request -> Event Entity
   */
  public static Event toEntity(Match match, EventCreateRequest req) {
    return Event.builder()
        .match(match)
        .title(req.getTitle())
        .description(req.getDescription())
        .eventAt(req.getEventAt())
        .repeatType(req.getRepeatType())
        .alarmOption(req.getAlarmOption())
        .build();
  }

  public static EventResponse toResponse(Event e, LocalDate occurrenceDate) {
    return EventResponse.builder()
        .eventId(e.getId())
        .title(e.getTitle())
        .description(e.getDescription())
        .eventAt(occurrenceDate)
        .repeatType(e.getRepeatType())
        .alarmOption(e.getAlarmOption())
        .anniversary(e.isAnniversary())
        .createdAt(e.getCreatedAt())
        .updatedAt(e.getUpdatedAt())
        .build();
  }
}
