package com.qmate.domain.event.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;

import com.qmate.domain.event.model.request.EventCreateRequest;
import com.qmate.domain.event.model.request.EventUpdateRequest;
import com.qmate.domain.event.model.response.CalendarMonthResponse;
import com.qmate.domain.event.model.response.EventResponse;
import com.qmate.domain.event.entity.Event;
import com.qmate.domain.event.entity.EventAlarmOption;
import com.qmate.domain.event.entity.EventRepeatType;
import com.qmate.domain.event.repository.EventRepository;
import com.qmate.domain.match.Match;
import com.qmate.domain.match.repository.MatchRepository;
import com.qmate.exception.custom.event.EventCalendarDateRangeExceededException;
import com.qmate.exception.custom.event.EventDeletionNotAllowedException;
import com.qmate.exception.custom.event.EventListDateRangeExceededException;
import com.qmate.exception.custom.event.EventNotFoundException;
import com.qmate.exception.custom.event.EventRepeatModificationNotAllowedException;
import com.qmate.exception.custom.matchinstance.MatchNotFoundException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

  @Mock private MatchRepository matchRepository;
  @Mock private EventRepository eventRepository;

  @InjectMocks private EventService eventService;

  @Test
  @DisplayName("일정 생성 - 성공")
  void createEvent_success() {
    // given
    Long matchId = 1L;
    Long userId = 99L;

    Match match = Match.builder().id(matchId).build();
    given(matchRepository.findAuthorizedById(matchId, userId)).willReturn(Optional.of(match));

    EventCreateRequest req = EventCreateRequest.builder()
        .title("제목")
        .description("설명")
        .eventAt(LocalDate.of(2025, 10, 9))
        .repeatType(EventRepeatType.NONE)
        .alarmOption(EventAlarmOption.WEEK_BEFORE)
        .build();

    Event saved = Event.builder()
        .id(10L)
        .match(match)
        .title(req.getTitle())
        .description(req.getDescription())
        .eventAt(req.getEventAt())
        .repeatType(req.getRepeatType())
        .alarmOption(req.getAlarmOption())
        .anniversary(false)
        .createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .build();
    given(eventRepository.save(any(Event.class))).willReturn(saved);

    // when
    EventResponse res = eventService.createEvent(matchId, userId, req);

    // then
    assertThat(res.getEventId()).isEqualTo(10L);
    assertThat(res.getTitle()).isEqualTo("제목");
    assertThat(res.getRepeatType()).isEqualTo(EventRepeatType.NONE);
    assertThat(res.getAlarmOption()).isEqualTo(EventAlarmOption.WEEK_BEFORE);

    // verify -> then().should()
    then(matchRepository).should().findAuthorizedById(matchId, userId);
    then(eventRepository).should().save(any(Event.class));
  }

  @Test
  @DisplayName("일정 생성 - 권한/존재 실패 시 404")
  void createEvent_matchNotFound() {
    // given
    Long matchId = 1L;
    Long userId = 99L;
    given(matchRepository.findAuthorizedById(matchId, userId)).willReturn(Optional.empty());

    EventCreateRequest req = EventCreateRequest.builder()
        .title("제목")
        .eventAt(LocalDate.of(2025, 10, 9))
        .repeatType(EventRepeatType.NONE)
        .alarmOption(EventAlarmOption.WEEK_BEFORE)
        .build();

    // expect
    assertThatThrownBy(() -> eventService.createEvent(matchId, userId, req))
        .isInstanceOf(MatchNotFoundException.class);
  }

  @Test
  @DisplayName("상세 조회 - 성공")
  void getEvent_success() {
    // given
    Long matchId = 1L, userId = 99L, eventId = 10L;

    Match match = Match.builder().id(matchId).build();
    Event event = Event.builder()
        .id(eventId)
        .match(match)
        .title("제목")
        .description("설명")
        .eventAt(LocalDate.of(2025, 10, 9))
        .repeatType(EventRepeatType.NONE)
        .alarmOption(EventAlarmOption.WEEK_BEFORE)
        .anniversary(false)
        .createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .build();

    given(eventRepository.findAuthorizedById(matchId, userId, eventId))
        .willReturn(Optional.of(event));

    // when
    EventResponse res = eventService.getEvent(matchId, userId, eventId);

    // then
    assertThat(res.getEventId()).isEqualTo(eventId);
    assertThat(res.getTitle()).isEqualTo("제목");
    assertThat(res.getRepeatType()).isEqualTo(EventRepeatType.NONE);
    assertThat(res.getAlarmOption()).isEqualTo(EventAlarmOption.WEEK_BEFORE);

    then(eventRepository).should().findAuthorizedById(matchId, userId, eventId);
  }

  @Test
  @DisplayName("상세 조회 - 권한/존재 실패 시 404")
  void getEvent_notFound() {
    // given
    Long matchId = 1L, userId = 99L, eventId = 10L;
    given(eventRepository.findAuthorizedById(matchId, userId, eventId))
        .willReturn(Optional.empty());

    // expect
    assertThatThrownBy(() -> eventService.getEvent(matchId, userId, eventId))
        .isInstanceOf(EventNotFoundException.class);
  }

  @Test
  @DisplayName("일정 수정 - 성공(일부 필드만 갱신, updatedAt 갱신)")
  void updateEvent_success() {
    // given
    Long matchId = 1L, userId = 99L, eventId = 10L;
    Match match = Match.builder().id(matchId).build();
    Event event = Event.builder()
        .id(eventId)
        .match(match)
        .title("old")
        .description("desc")
        .eventAt(LocalDate.of(2025, 10, 9))
        .repeatType(EventRepeatType.NONE)
        .alarmOption(EventAlarmOption.WEEK_BEFORE)
        .anniversary(false)
        .createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now().minusDays(1))
        .build();

    given(eventRepository.findAuthorizedById(matchId, userId, eventId))
        .willReturn(Optional.of(event));

    EventUpdateRequest req = EventUpdateRequest.builder()
        .title("new-title")
        .repeatType(EventRepeatType.MONTHLY) // 기념일 아님 → 변경 허용
        .build();

    // saveAndFlush 후 updatedAt이 최신으로 들어간다고 가정하고 same event 반환
    given(eventRepository.saveAndFlush(any(Event.class))).willAnswer(inv -> inv.getArgument(0));

    // when
    EventResponse res = eventService.updateEvent(matchId, userId, eventId, req);

    // then
    assertThat(res.getTitle()).isEqualTo("new-title");
    assertThat(res.getRepeatType()).isEqualTo(EventRepeatType.MONTHLY);
    then(eventRepository).should().findAuthorizedById(matchId, userId, eventId);
    then(eventRepository).should().saveAndFlush(any(Event.class));
  }

  @Test
  @DisplayName("일정 수정 - 기념일 이벤트는 repeatType 변경 불가")
  void updateEvent_anniversary_repeatChange_forbidden() {
    // given
    Long matchId = 1L, userId = 99L, eventId = 10L;
    Match match = Match.builder().id(matchId).build();
    Event anniversary = Event.builder()
        .id(eventId)
        .match(match)
        .title("anniv")
        .eventAt(LocalDate.of(2025,10,9))
        .repeatType(EventRepeatType.NONE)
        .alarmOption(EventAlarmOption.WEEK_BEFORE)
        .anniversary(true)
        .createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .build();

    given(eventRepository.findAuthorizedById(matchId, userId, eventId))
        .willReturn(Optional.of(anniversary));

    EventUpdateRequest req = EventUpdateRequest.builder()
        .repeatType(EventRepeatType.YEARLY) // 금지
        .build();

    // expect
    assertThatThrownBy(() -> eventService.updateEvent(matchId, userId, eventId, req))
        .isInstanceOf(EventRepeatModificationNotAllowedException.class);

    then(eventRepository).should().findAuthorizedById(matchId, userId, eventId);
    then(eventRepository).shouldHaveNoMoreInteractions();
  }

  @Test
  @DisplayName("일정 삭제 - 성공")
  void deleteEvent_success() {
    // given
    Long matchId = 1L, userId = 99L, eventId = 10L;
    Event e = Event.builder()
        .id(eventId)
        .match(Match.builder().id(matchId).build())
        .title("t")
        .eventAt(LocalDate.of(2025,10,9))
        .repeatType(EventRepeatType.NONE)
        .alarmOption(EventAlarmOption.SAME_DAY)
        .anniversary(false)
        .createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .build();

    given(eventRepository.findAuthorizedById(matchId, userId, eventId)).willReturn(Optional.of(e));

    // when
    eventService.deleteEvent(matchId, userId, eventId);

    // then
    then(eventRepository).should().findAuthorizedById(matchId, userId, eventId);
    then(eventRepository).should().delete(e);
  }

  @Test
  @DisplayName("일정 삭제 - 기념일 이벤트는 삭제 불가")
  void deleteEvent_anniversary_forbidden() {
    // given
    Long matchId = 1L, userId = 99L, eventId = 10L;
    Event e = Event.builder()
        .id(eventId)
        .match(Match.builder().id(matchId).build())
        .title("anniv")
        .eventAt(LocalDate.of(2025,10,9))
        .repeatType(EventRepeatType.NONE)
        .alarmOption(EventAlarmOption.WEEK_BEFORE)
        .anniversary(true)
        .createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .build();

    given(eventRepository.findAuthorizedById(matchId, userId, eventId)).willReturn(Optional.of(e));

    // expect
    assertThatThrownBy(() -> eventService.deleteEvent(matchId, userId, eventId))
        .isInstanceOf(EventDeletionNotAllowedException.class);

    then(eventRepository).should().findAuthorizedById(matchId, userId, eventId);
    then(eventRepository).should(never()).delete(any(Event.class));
  }

  @Test
  @DisplayName("listEvents - WEEKLY 전개: from << seed, to 포함까지 전개")
  void listEvents_weekly_expand_from_before_seed() {
    // given
    long matchId = 1L, userId = 99L;
    LocalDate seed = LocalDate.of(2025, 10, 1); // 수요일
    Event weekly = Event.builder()
        .id(100L)
        .match(Match.builder().id(matchId).build())
        .title("weekly")
        .eventAt(seed)
        .repeatType(EventRepeatType.WEEKLY)
        .anniversary(false)
        .createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .build();

    LocalDate from = LocalDate.of(2025, 7, 29); // 화
    LocalDate to   = LocalDate.of(2025, 10, 15); // 수

    given(eventRepository.findCandidates(eq(matchId), eq(userId), eq(from), eq(to), isNull(), isNull()))
        .willReturn(List.of(weekly));

    // when
    Page<EventResponse> page = eventService.listEvents(
        matchId, userId, from, to, null, null, PageRequest.of(0, 10)
    );

    // then: 10/01, 10/08, 10/15
    assertThat(page.getContent()).extracting(EventResponse::getEventAt)
        .containsExactly(
            LocalDate.of(2025, 10, 1),
            LocalDate.of(2025, 10, 8),
            LocalDate.of(2025, 10, 15)
        );
  }

  @Test
  @DisplayName("listEvents - MONTHLY 전개: 8/31 → 9/30, 10/31, 11/30 (말일 클램프)")
  void listEvents_monthly_end_of_month_clamp() {
    // given
    long matchId = 1L, userId = 99L;
    Event monthly = Event.builder()
        .id(101L)
        .match(Match.builder().id(matchId).build())
        .title("monthly-31")
        .eventAt(LocalDate.of(2025, 8, 31))
        .repeatType(EventRepeatType.MONTHLY)
        .anniversary(false)
        .createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .build();

    LocalDate from = LocalDate.of(2025, 9, 15);
    LocalDate to   = LocalDate.of(2025, 11, 30);

    given(eventRepository.findCandidates(eq(matchId), eq(userId), eq(from), eq(to), isNull(), isNull()))
        .willReturn(List.of(monthly));

    // when
    Page<EventResponse> page = eventService.listEvents(
        matchId, userId, from, to, null, null, PageRequest.of(0, 10)
    );

    // then
    assertThat(page.getContent()).extracting(EventResponse::getEventAt)
        .containsExactly(
            LocalDate.of(2025, 9, 30),
            LocalDate.of(2025, 10, 31),
            LocalDate.of(2025, 11, 30)
        );
  }

  @Test
  @DisplayName("listEvents - YEARLY 전개: 2/29는 평년 2/28로 보정")
  void listEvents_yearly_feb29_to_feb28_in_common_years() {
    // given
    long matchId = 1L, userId = 99L;
    Event yearly = Event.builder()
        .id(102L)
        .match(Match.builder().id(matchId).build())
        .title("yearly-229")
        .eventAt(LocalDate.of(2020, 2, 29))
        .repeatType(EventRepeatType.YEARLY)
        .anniversary(true)
        .createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .build();

    LocalDate from = LocalDate.of(2025, 1, 1);
    LocalDate to   = LocalDate.of(2027, 12, 31);

    given(eventRepository.findCandidates(eq(matchId), eq(userId), eq(from), eq(to), isNull(), isNull()))
        .willReturn(List.of(yearly));

    // when
    Page<EventResponse> page = eventService.listEvents(
        matchId, userId, from, to, null, null, PageRequest.of(0, 10)
    );

    // then: 2025/02/28, 2026/02/28, 2027/02/28
    assertThat(page.getContent()).extracting(EventResponse::getEventAt)
        .containsExactly(
            LocalDate.of(2025, 2, 28),
            LocalDate.of(2026, 2, 28),
            LocalDate.of(2027, 2, 28)
        );
  }

  @Test
  @DisplayName("listEvents - NONE: from/to 경계 포함")
  void listEvents_none_inclusive_bounds() {
    // given
    long matchId = 1L, userId = 99L;
    LocalDate from = LocalDate.of(2025, 10, 10);
    LocalDate to   = LocalDate.of(2025, 10, 31);

    Event atFrom = Event.builder()
        .id(201L).match(Match.builder().id(matchId).build())
        .title("at-from").eventAt(from)
        .repeatType(EventRepeatType.NONE).anniversary(false)
        .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now())
        .build();

    Event atTo = Event.builder()
        .id(202L).match(Match.builder().id(matchId).build())
        .title("at-to").eventAt(to)
        .repeatType(EventRepeatType.NONE).anniversary(false)
        .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now())
        .build();

    given(eventRepository.findCandidates(eq(matchId), eq(userId), eq(from), eq(to), isNull(), isNull()))
        .willReturn(List.of(atFrom, atTo));

    // when
    Page<EventResponse> page = eventService.listEvents(
        matchId, userId, from, to, null, null, PageRequest.of(0, 10)
    );

    // then
    assertThat(page.getContent()).extracting(EventResponse::getEventAt)
        .containsExactlyInAnyOrder(from, to);
  }

  @Test
  @DisplayName("listEvents - 정렬 타이브레이커: 동일 발생일이면 eventId 오름차순")
  void listEvents_sort_tie_breaker_by_eventId() {
    // given (동일 발생일 2025-10-10)
    long matchId = 1L, userId = 99L;
    LocalDate from = LocalDate.of(2025, 10, 1);
    LocalDate to   = LocalDate.of(2025, 10, 31);

    Event e1 = Event.builder()
        .id(1L).match(Match.builder().id(matchId).build())
        .title("A").eventAt(LocalDate.of(2025, 10, 10))
        .repeatType(EventRepeatType.NONE).anniversary(false)
        .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now())
        .build();

    Event e2 = Event.builder()
        .id(2L).match(Match.builder().id(matchId).build())
        .title("B").eventAt(LocalDate.of(2025, 10, 10))
        .repeatType(EventRepeatType.NONE).anniversary(false)
        .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now())
        .build();

    given(eventRepository.findCandidates(eq(matchId), eq(userId), eq(from), eq(to), isNull(), isNull()))
        .willReturn(List.of(e2, e1)); // 섞어서 주입

    // when
    Page<EventResponse> page = eventService.listEvents(
        matchId, userId, from, to, null, null, PageRequest.of(0, 10)
    );

    // then: eventId 1 → 2 순으로 정렬되어야 함
    assertThat(page.getContent()).extracting(EventResponse::getEventId)
        .containsExactly(1L, 2L);
  }

  @Test
  @DisplayName("listEvents - 페이징: page=1,size=2 슬라이싱")
  void listEvents_paging_slice() {
    // given: 주 1회, to를 10/29까지(총 5회: 1,8,15,22,29)
    long matchId = 1L, userId = 99L;
    Event weekly = Event.builder()
        .id(300L)
        .match(Match.builder().id(matchId).build())
        .title("weekly")
        .eventAt(LocalDate.of(2025, 10, 1))
        .repeatType(EventRepeatType.WEEKLY)
        .anniversary(false)
        .createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .build();

    LocalDate from = LocalDate.of(2025, 7, 1);
    LocalDate to   = LocalDate.of(2025, 10, 29);

    given(eventRepository.findCandidates(eq(matchId), eq(userId), eq(from), eq(to), isNull(), isNull()))
        .willReturn(List.of(weekly));

    // when: page=1,size=2 → [10/15, 10/22] 기대
    Page<EventResponse> page = eventService.listEvents(
        matchId, userId, from, to, null, null, PageRequest.of(1, 2)
    );

    // then
    assertThat(page.getTotalElements()).isEqualTo(5);
    assertThat(page.getContent()).extracting(EventResponse::getEventAt)
        .containsExactly(
            LocalDate.of(2025, 10, 15),
            LocalDate.of(2025, 10, 22)
        );
  }

  @Test
  @DisplayName("listEvents - 조회 기간 3년 초과 시 예외")
  void listEvents_range_exceeded_throws() {
    // given
    long matchId = 1L, userId = 99L;
    LocalDate from = LocalDate.of(2020, 1, 1);
    LocalDate to   = LocalDate.of(2024, 1, 2); // from.plusYears(3) 초과

    // when & then
    assertThatThrownBy(() ->
        eventService.listEvents(matchId, userId, from, to, null, null, PageRequest.of(0, 20))
    ).isInstanceOf(EventListDateRangeExceededException.class);

    // findCandidates 호출되지 않아야 함
    then(eventRepository).should(never())
        .findCandidates(anyLong(), anyLong(), any(LocalDate.class), any(LocalDate.class), any(), any());
  }

  private static final Long MATCH_ID = 42L;
  private static final Long USER_ID = 7L;

  @Nested
  @DisplayName("캘린더 조회(getCalendarMonth)")
  class GetCalendarMonth {

    @Test
    @DisplayName("to < from 이면 빈 결과를 반환한다")
    void returnsEmptyWhenReversedRange() {
      // given
      LocalDate from = LocalDate.of(2025, 9, 10);
      LocalDate to = LocalDate.of(2025, 9, 9);

      // when
      CalendarMonthResponse res = eventService.getCalendarMonth(MATCH_ID, USER_ID, from, to);

      // then
      assertThat(res.getYear()).isEqualTo(YearMonth.from(from).getYear());
      assertThat(res.getMonth()).isEqualTo(YearMonth.from(from).getMonthValue());
      assertThat(res.getDays()).isEmpty();

      then(eventRepository).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("조회 기간이 60일 초과면 예외를 던진다")
    void throwsWhenOver60Days() {
      // given
      LocalDate from = LocalDate.of(2025, 9, 1);
      LocalDate to = from.plusDays(60); // 포함 기준 61일 → 예외

      // when / then
      assertThatThrownBy(() ->
          eventService.getCalendarMonth(MATCH_ID, USER_ID, from, to)
      ).isInstanceOf(EventCalendarDateRangeExceededException.class);

      then(eventRepository).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("같은 날짜에 여러 이벤트가 있으면 대표 eventId=최솟값, isAnniversary는 OR 집계")
    void aggregatesSameDate() {
      // given
      LocalDate from = LocalDate.of(2025, 9, 1);
      LocalDate to = LocalDate.of(2025, 9, 30);
      LocalDate day = LocalDate.of(2025, 9, 27);

      Event e1 = mock(Event.class);
      given(e1.getId()).willReturn(10L);
      given(e1.isAnniversary()).willReturn(false);
      given(e1.getRepeatType()).willReturn(EventRepeatType.NONE);
      given(e1.getEventAt()).willReturn(day);

      Event e2 = mock(Event.class);
      given(e2.getId()).willReturn(3L); // 더 작은 ID
      given(e2.isAnniversary()).willReturn(true); // 기념일
      given(e2.getRepeatType()).willReturn(EventRepeatType.NONE);
      given(e2.getEventAt()).willReturn(day);

      // findCandidates는 두 이벤트를 반환
      given(eventRepository.findCandidates(MATCH_ID, USER_ID, from, to, null, null))
          .willReturn(List.of(e1, e2));

      // when
      CalendarMonthResponse res = eventService.getCalendarMonth(MATCH_ID, USER_ID, from, to);

      // then
      assertThat(res.getDays()).hasSize(1);
      var item = res.getDays().get(0);
      assertThat(item.getEventAt()).isEqualTo(day);
      assertThat(item.getEventId()).isEqualTo(3L); // 최솟값
      assertThat(item.isAnniversary()).isTrue();   // OR → true

      then(eventRepository).should().findCandidates(MATCH_ID, USER_ID, from, to, null, null);
    }

    @Test
    @DisplayName("WEEKLY 반복 전개: 기간 내 해당 요일로 전개되어 날짜당 1개로 반환된다")
    void expandsWeekly() {
      // given
      LocalDate from = LocalDate.of(2025, 9, 1);   // 월
      LocalDate to = LocalDate.of(2025, 9, 21);    // 3주 범위

      // seed: 2025-09-03(수) → 매주 수요일 전개 예상: 09-03, 09-10, 09-17
      Event weekly = mock(Event.class);
      given(weekly.getId()).willReturn(100L);
      given(weekly.isAnniversary()).willReturn(false);
      given(weekly.getRepeatType()).willReturn(EventRepeatType.WEEKLY);
      given(weekly.getEventAt()).willReturn(LocalDate.of(2025, 9, 3));

      // findCandidates는 1건 반환
      given(eventRepository.findCandidates(MATCH_ID, USER_ID, from, to, null, null))
          .willReturn(List.of(weekly));

      // when
      CalendarMonthResponse res = eventService.getCalendarMonth(MATCH_ID, USER_ID, from, to);

      // then
      assertThat(res.getDays())
          .extracting(d -> d.getEventAt())
          .containsExactly(
              LocalDate.of(2025, 9, 3),
              LocalDate.of(2025, 9, 10),
              LocalDate.of(2025, 9, 17)
          );

      // 날짜당 1개 보장 & 동일 이벤트의 반복 전개이므로 eventId 동일
      assertThat(res.getDays())
          .allSatisfy(d -> assertThat(d.getEventId()).isEqualTo(100L));

      then(eventRepository).should().findCandidates(MATCH_ID, USER_ID, from, to, null, null);
    }
  }
}
