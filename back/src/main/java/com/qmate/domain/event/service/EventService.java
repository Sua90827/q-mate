package com.qmate.domain.event.service;

import com.qmate.common.constants.event.EventConstants;
import com.qmate.domain.event.entity.Event;
import com.qmate.domain.event.entity.EventRepeatType;
import com.qmate.domain.event.mapper.EventMapper;
import com.qmate.domain.event.model.request.EventCreateRequest;
import com.qmate.domain.event.model.request.EventUpdateRequest;
import com.qmate.domain.event.model.response.CalendarMonthResponse;
import com.qmate.domain.event.model.response.EventResponse;
import com.qmate.domain.event.repository.EventRepository;
import com.qmate.domain.match.Match;
import com.qmate.domain.match.repository.MatchRepository;
import com.qmate.exception.custom.event.EventCalendarDateRangeExceededException;
import com.qmate.exception.custom.event.EventDeletionNotAllowedException;
import com.qmate.exception.custom.event.EventListDateRangeExceededException;
import com.qmate.exception.custom.event.EventNotFoundException;
import com.qmate.exception.custom.event.EventRepeatModificationNotAllowedException;
import com.qmate.exception.custom.matchinstance.MatchNotFoundException;
import jakarta.annotation.Nullable;
import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EventService {

  private final EventRepository eventRepository;
  private final MatchRepository matchRepository;

  /**
   * 일정 생성
   */
  public EventResponse createEvent(Long matchId, Long userId, EventCreateRequest request) {
    Match match = matchRepository.findAuthorizedById(matchId, userId)
        .orElseThrow(MatchNotFoundException::new); // 권한/존재 통합 검증 쿼리

    Event event = EventMapper.toEntity(match, request);
    Event saved = eventRepository.save(event);

    return EventMapper.toResponse(saved);
  }

  /**
   * 일정 단건 조회
   */
  @Transactional(readOnly = true)
  public EventResponse getEvent(Long matchId, Long userId, Long eventId) {
    Event event = eventRepository.findAuthorizedById(matchId, userId, eventId)
        .orElseThrow(EventNotFoundException::new);
    return EventMapper.toResponse(event);
  }

  /**
   * 일정 수정
   */
  @Transactional
  public EventResponse updateEvent(Long matchId, Long userId, Long eventId, EventUpdateRequest req) {
    Event event = eventRepository.findAuthorizedById(matchId, userId, eventId)
        .orElseThrow(EventNotFoundException::new);

    // 기념일이면 반복 설정 변경 금지
    if (event.isAnniversary() && req.getRepeatType() != null
        && req.getRepeatType() != event.getRepeatType()) {
      throw new EventRepeatModificationNotAllowedException();
    }

    // null 이 아닌 항목만 반영
    if (req.getTitle() != null) {
      event.setTitle(req.getTitle());
    }
    if (req.getDescription() != null) {
      event.setDescription(req.getDescription());
    }
    if (req.getEventAt() != null) {
      event.setEventAt(req.getEventAt());
    }
    if (req.getRepeatType() != null) {
      event.setRepeatType(req.getRepeatType());
    }
    if (req.getAlarmOption() != null) {
      event.setAlarmOption(req.getAlarmOption());
    }
    return EventMapper.toResponse(eventRepository.saveAndFlush(event));
  }

  /**
   * 일정 삭제
   */
  public void deleteEvent(Long matchId, Long userId, Long eventId) {
    Event event = eventRepository.findAuthorizedById(matchId, userId, eventId)
        .orElseThrow(EventNotFoundException::new);

    if (event.isAnniversary()) {
      throw new EventDeletionNotAllowedException();
    }

    eventRepository.delete(event);
  }

  /**
   * 일정 목록 조회 (반복 전개 포함)
   */
  @Transactional(readOnly = true)
  public Page<EventResponse> listEvents(
      Long matchId, Long userId,
      LocalDate from, LocalDate to,
      @Nullable EventRepeatType repeatTypeFilter,
      @Nullable Boolean anniversaryFilter,
      Pageable pageable
  ) {
    // 역전 가드
    if (to.isBefore(from)) {
      return Page.empty(pageable);
    }

    // 최대 N년 범위 제한
    if (to.isAfter(from.plusYears(EventConstants.EVENT_LIST_MAX_RANGE_YEARS))) {
      throw new EventListDateRangeExceededException();
    }

    // 후보 이벤트(권한 포함) 조회
    List<Event> candidates = eventRepository.findCandidates(
        matchId, userId, from, to, repeatTypeFilter, anniversaryFilter
    );

    // 반복 전개
    List<Occurrence> occurrences = new ArrayList<>();
    for (Event e : candidates) {
      for (LocalDate d : expandOccurrences(e, from, to)) {
        occurrences.add(new Occurrence(d, e));
      }
    }

    // 정렬: 발생일 ASC, eventId ASC
    occurrences.sort(
        Comparator.comparing(Occurrence::date)
            .thenComparing(o -> o.event().getId())
    );

    // 페이징
    int total = occurrences.size();
    int fromIdx = (int) pageable.getOffset();
    int toIdx = Math.min(fromIdx + pageable.getPageSize(), total);

    List<EventResponse> content = Collections.emptyList();
    if (fromIdx < toIdx) {
      content = occurrences.subList(fromIdx, toIdx).stream()
          .map(o -> EventMapper.toResponse(o.event(), o.date()))
          .toList();
    }

    return new PageImpl<>(content, pageable, total);
  }

  /**
   * 캘린더용 일정 조회
   * - from~to: 최대 60일(양끝 포함), 같은 달 제한 없음
   * - 날짜당 1개로 압축: 대표 eventId=최솟값, isAnniversary=OR
   * - 반복 전개(expandOccurrences) 사용
   */
  @Transactional(readOnly = true)
  public CalendarMonthResponse getCalendarMonth(
      Long matchId, Long userId,
      LocalDate from, LocalDate to
  ) {
    if (to.isBefore(from)) {
      YearMonth base = YearMonth.from(from);
      return CalendarMonthResponse.builder()
          .year(base.getYear())
          .month(base.getMonthValue())
          .days(List.of())
          .build();
    }

    // 최대 60일 (양끝 포함): between은 양끝 제외라서 59 초과면 60일 초과
    long gap = ChronoUnit.DAYS.between(from, to);
    if (gap > EventConstants.EVENT_CALENDAR_MAX_RANGE_DAYS - 1) {
      throw new EventCalendarDateRangeExceededException();
    }

    YearMonth base = YearMonth.from(from); // 응답 year/month 표기를 위한 기준

    // 후보 조회 (권한 포함 가정)
    List<Event> candidates = eventRepository.findCandidates(
        matchId, userId, from, to, null, null
    );

    // 반복 전개
    List<Occurrence> occurrences = new ArrayList<>();
    for (Event e : candidates) {
      for (LocalDate d : expandOccurrences(e, from, to)) {
        occurrences.add(new Occurrence(d, e));
      }
    }

    // 날짜별 압축: 대표 eventId=최솟값, isAnniversary=OR
    Map<LocalDate, Aggregate> byDate = new TreeMap<>();
    for (Occurrence o : occurrences) {
      LocalDate date = o.date();
      Event ev = o.event();
      Aggregate agg = byDate.get(date);
      if (agg == null) {
        byDate.put(date, new Aggregate(ev.getId(), ev.isAnniversary()));
      } else {
        byDate.put(date, new Aggregate(
            Math.min(agg.representativeEventId(), ev.getId()),
            agg.isAnniversary() || ev.isAnniversary()
        ));
      }
    }

    List<CalendarMonthResponse.DayItem> days = byDate.entrySet().stream()
        .map(e -> CalendarMonthResponse.DayItem.builder()
            .eventId(e.getValue().representativeEventId())
            .eventAt(e.getKey())
            .isAnniversary(e.getValue().isAnniversary())
            .build())
        .toList();

    return CalendarMonthResponse.builder()
        .year(base.getYear())
        .month(base.getMonthValue())
        .days(days)
        .build();
  }

  /* ===== 반복 전개 ===== */

  private List<LocalDate> expandOccurrences(Event e, LocalDate from, LocalDate to) {
    LocalDate seed = e.getEventAt();
    return switch (e.getRepeatType()) {
      case NONE -> (seed.isBefore(from) || seed.isAfter(to)) ? List.of() : List.of(seed);
      case WEEKLY -> expandWeekly(seed, from, to);
      case MONTHLY -> expandMonthly(seed, from, to);
      case YEARLY -> expandYearly(seed, from, to);
    };
  }

  private List<LocalDate> expandWeekly(LocalDate seed, LocalDate from, LocalDate to) {

    // seed의 요일에 맞춘 첫 발생일 계산
    int shift = (seed.getDayOfWeek().getValue() - from.getDayOfWeek().getValue() + 7) % 7;
    LocalDate start = from.plusDays(shift);
    // ex) seed 목(4), from: 10-06 월(1) -> shift 3 -> start 10-09 목
    // ex) seed 월(1), from: 10-09 목(4) -> shift 4 -> start 10-13 월

    // start가 seed 이전이면 seed로 조정
    if (start.isBefore(seed)) {
      start = seed;
    }

    if (start.isAfter(to)) {
      return List.of();
    }
    List<LocalDate> out = new ArrayList<>();
    // start부터 to까지 1주 단위로 증가시키며 추가
    for (LocalDate d = start; !d.isAfter(to); d = d.plusWeeks(1)) {
      out.add(d);
    }
    return out;
  }

  private List<LocalDate> expandMonthly(LocalDate seed, LocalDate from, LocalDate to) {

    if (seed.isAfter(to)) {
      return List.of();
    }

    List<LocalDate> out = new ArrayList<>();

    // 둘 중 더 큰 달을 선택
    YearMonth ym = YearMonth.from(from.isBefore(seed) ? seed : from);

    // seed의 날짜가 ym의 말일을 초과하는 경우 ym의 말일로 조정
    LocalDate first = clampDay(ym, seed.getDayOfMonth());

    // ex) seed 8월 31일, from 9월 15일 -> ym 9월, first 9월 30일
    // ex) seed 8월 31일, from 7월 30일 -> ym 8월, first 8월 31일
    // ex) seed 25년 12월 31일, from 26년 1월 30일 -> 시작 26년 1월 31일

    // first가 from 이전이면 다음 달부터 시작
    if (first.isBefore(from)) {
      ym = ym.plusMonths(1);
      first = clampDay(ym, seed.getDayOfMonth());
    }

    // to 까지 월 단위로 증가시키며 추가
    for (LocalDate d = first; !d.isAfter(to); ym = ym.plusMonths(1), d = clampDay(ym, seed.getDayOfMonth())) {
      if (!d.isBefore(from)) {
        out.add(d);
      }
    }
    return out;
  }

  private List<LocalDate> expandYearly(LocalDate seed, LocalDate from, LocalDate to) {

    if (seed.isAfter(to)) {
      return List.of();
    }

    List<LocalDate> out = new ArrayList<>();

    // 시작 연도 계산: from 기준으로 seed의 '월일'을 맞춘 첫 해
    int year = Math.max(from.getYear(), seed.getYear());

    LocalDate d = toYearlyDate(year, seed.getMonthValue(), seed.getDayOfMonth());

    // from 이전이면 다음 해부터 시작
    if (d.isBefore(from)) {
      year++;
      d = toYearlyDate(year, seed.getMonthValue(), seed.getDayOfMonth());
    }

    // to 까지 연 단위로 증가시키며 추가
    while (!d.isAfter(to)) {
      out.add(d);
      year++;
      d = toYearlyDate(year, seed.getMonthValue(), seed.getDayOfMonth());
    }
    return out;
  }

  // 기준 날짜가 해당 월의 말일을 초과하는 경우면 해당 월의 말일로 조정
  // ex) 해당 월이 9월, 기준 날짜가 31일이면 9월 30일로 조정
  private LocalDate clampDay(YearMonth ym, int dayOfMonth) {
    int dom = Math.min(dayOfMonth, ym.lengthOfMonth());
    return LocalDate.of(ym.getYear(), ym.getMonth(), dom);
  }

  // 윤년 고려: 2월 29일인 경우, 평년에는 2월 28일로 조정
  private LocalDate toYearlyDate(int year, int month, int day) {
    if (month == 2 && day == 29 && !Year.isLeap(year)) {
      return LocalDate.of(year, 2, 28);
    }
    return LocalDate.of(year, month, day); // 그 외는 그대로
  }

  private record Occurrence(LocalDate date, Event event) {

  }

  private record Aggregate(long representativeEventId, boolean isAnniversary) {

  }
}
