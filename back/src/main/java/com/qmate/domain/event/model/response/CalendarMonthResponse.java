// CalendarMonthResponse.java
package com.qmate.domain.event.model.response;

import java.time.LocalDate;
import java.util.List;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CalendarMonthResponse {

  private int year;       // 1~9999
  private int month;      // 1~12
  private List<DayItem> days;

  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class DayItem {

    private Long eventId;         // 같은 날짜 여러 개면 대표 eventId(최솟값)
    private LocalDate eventAt;    // yyyy-MM-dd
    private boolean isAnniversary;// 하나라도 기념일이면 true
  }
}
