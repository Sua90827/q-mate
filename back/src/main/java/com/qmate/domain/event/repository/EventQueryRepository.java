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

  /**
   * 오늘/3일후/7일후 알림 대상 "이벤트"만 조회
   * 매치 ACTIVE 검증 포함
   */
  List<DueEventRow> findDueEventAlarmRows(LocalDate today);

  record DueEventRow(
      String code,
      Long eventId,
      Long matchId,
      String title,
      LocalDate occurDate
  ) {}
}
