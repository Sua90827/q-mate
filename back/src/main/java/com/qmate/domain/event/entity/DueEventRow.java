package com.qmate.domain.event.entity;

import java.time.LocalDate;

public record DueEventRow(
    String code,
    Long eventId,
    Long matchId,
    String title,
    LocalDate occurDate
) {}
