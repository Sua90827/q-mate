package com.qmate.domain.event;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

import com.qmate.domain.event.model.request.EventCreateRequest;
import com.qmate.domain.event.model.request.EventUpdateRequest;
import com.qmate.domain.event.model.response.EventResponse;
import com.qmate.domain.event.entity.Event;
import com.qmate.domain.event.entity.EventAlarmOption;
import com.qmate.domain.event.entity.EventRepeatType;
import com.qmate.domain.event.repository.EventRepository;
import com.qmate.domain.event.service.EventService;
import com.qmate.domain.match.Match;
import com.qmate.domain.match.repository.MatchRepository;
import com.qmate.exception.custom.event.EventDeletionNotAllowedException;
import com.qmate.exception.custom.event.EventNotFoundException;
import com.qmate.exception.custom.event.EventRepeatModificationNotAllowedException;
import com.qmate.exception.custom.matchinstance.MatchNotFoundException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
}
