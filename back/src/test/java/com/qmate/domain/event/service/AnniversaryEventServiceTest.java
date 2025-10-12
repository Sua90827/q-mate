package com.qmate.domain.event.service;

import com.qmate.common.constants.event.EventConstants;
import com.qmate.domain.event.entity.Event;
import com.qmate.domain.event.entity.EventRepeatType;
import com.qmate.domain.event.repository.EventRepository;
import com.qmate.domain.match.Match;
import com.qmate.domain.match.RelationType;
import com.qmate.domain.user.User;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AnniversaryEventServiceTest {

  @Mock
  private EventRepository eventRepository;

  @InjectMocks
  private EventAnniversaryService eventAnniversaryService;

  private Match match;
  private User userA;
  private User userB;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    match = Match.builder()
        .id(1L)
        .relationType(RelationType.COUPLE)
        .startDate(LocalDateTime.now().minusDays(100))
        .build();

    userA = User.builder()
        .id(1L)
        .nickname("A")
        .birthDate(LocalDate.of(1995, 5, 10))
        .build();

    userB = User.builder()
        .id(2L)
        .nickname("B")
        .birthDate(LocalDate.of(1997, 12, 1))
        .build();
  }

  @Test
  void testCreateDefaultAnniversaries() {
    when(eventRepository.saveAll(any())).thenAnswer(inv -> inv.getArgument(0));

    List<Event> events = eventAnniversaryService.createDefaultAnniversaries(match, userA, userB);

    assertThat(events).isNotEmpty();
    assertThat(events.stream().anyMatch(e -> e.getTitle().equals("100일"))).isTrue();
    assertThat(events.stream().anyMatch(e -> e.getTitle().equals("주년"))).isTrue();
    assertThat(events.stream().anyMatch(e -> e.getTitle().contains(" 생일"))).isTrue();
  }

  @Test
  void testUpdateBirthdayEvents() {
    when(eventRepository.findBirthdayEventsForUserByMonthDay(any(), any(), anyInt(), anyInt()))
        .thenReturn(List.of(Event.builder().eventAt(LocalDate.of(1995, 5, 10)).build()));
    when(eventRepository.saveAll(any())).thenAnswer(inv -> inv.getArgument(0));

    LocalDate oldBirth = LocalDate.of(1995, 5, 10);
    LocalDate newBirth = LocalDate.of(1995, 6, 15);

    eventAnniversaryService.updateBirthdayEvents(1L, oldBirth, newBirth);
  }
  @Test
  void testUpdateMatchAnniversaries() {
    when(eventRepository.findHundredDayEventByMatchId(anyLong()))
        .thenReturn(Optional.of(Event.builder().eventAt(LocalDate.now()).build()));
    when(eventRepository.findAnniversaryEventByMatchId(anyLong()))
        .thenReturn(Optional.of(Event.builder().eventAt(LocalDate.now()).build()));

    eventAnniversaryService.updateMatchAnniversaries(match);

    verify(eventRepository, times(1)).findHundredDayEventByMatchId(eq(match.getId()));
    verify(eventRepository, times(1)).findAnniversaryEventByMatchId(eq(match.getId()));
  }

  @Test
  void testCreateDefaultAnniversaries_withNoStartDate() {
    // given
    match = Match.builder()
        .id(2L)
        .relationType(RelationType.COUPLE)
        .startDate(null)
        .build();

    when(eventRepository.saveAll(any())).thenAnswer(inv -> inv.getArgument(0));

    // when
    List<Event> events = eventAnniversaryService.createDefaultAnniversaries(match, userA, userB);

    // then
    // startDate가 없으므로 100일, 주년 이벤트는 생성되지 않아야 함
    assertThat(events.stream().noneMatch(e -> e.getTitle().equals("100일"))).isTrue();
    assertThat(events.stream().noneMatch(e -> e.getTitle().equals("주년"))).isTrue();
  }
}
