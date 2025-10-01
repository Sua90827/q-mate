package com.qmate.domain.event.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.qmate.domain.event.model.request.EventCreateRequest;
import com.qmate.domain.event.model.response.EventResponse;
import com.qmate.domain.event.entity.Event;
import com.qmate.domain.event.entity.EventAlarmOption;
import com.qmate.domain.event.entity.EventRepeatType;
import com.qmate.domain.event.repository.EventRepository;
import com.qmate.domain.match.Match;
import com.qmate.domain.match.repository.MatchRepository;
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
}
