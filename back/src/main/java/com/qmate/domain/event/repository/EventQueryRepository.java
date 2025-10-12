package com.qmate.domain.event.repository;

import com.qmate.domain.event.entity.Event;
import com.qmate.domain.event.entity.EventRepeatType;
import jakarta.annotation.Nullable;
import java.time.LocalDate;
import java.util.List;

public interface EventQueryRepository {

  List<Event> findCandidates(
      Long matchId,
      Long userId,
      LocalDate from,
      LocalDate to,
      @Nullable EventRepeatType repeatTypeFilter,
      @Nullable Boolean anniversaryFilter
  );

}
