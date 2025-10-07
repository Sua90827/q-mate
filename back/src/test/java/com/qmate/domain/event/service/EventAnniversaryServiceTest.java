package com.qmate.domain.event.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.*;
import static org.mockito.ArgumentMatchers.anyList;

import com.qmate.domain.event.entity.Event;
import com.qmate.domain.event.entity.EventAlarmOption;
import com.qmate.domain.event.entity.EventRepeatType;
import com.qmate.domain.event.repository.EventRepository;
import com.qmate.domain.match.RelationType;
import com.qmate.domain.match.Match;
import com.qmate.domain.user.User;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EventAnniversaryServiceTest {

  @Mock private EventRepository eventRepository;

  @InjectMocks private EventAnniversaryService service;

  @Captor private ArgumentCaptor<List<Event>> eventsCaptor;

  @Test
  void coupleWithStartDateAndBirthdays_createsFourEvents_andSavesAll() {
    // given
    Match match = mock(Match.class);
    given(match.getRelationType()).willReturn(RelationType.COUPLE);
    given(match.getStartDate()).willReturn(LocalDateTime.of(2025, 1, 1, 0, 0));

    User userA = mock(User.class);
    given(userA.getNickname()).willReturn("A");
    given(userA.getBirthDate()).willReturn(LocalDate.of(1990, 5, 10));

    User userB = mock(User.class);
    given(userB.getNickname()).willReturn("B");
    given(userB.getBirthDate()).willReturn(LocalDate.of(1992, 12, 3));

    // saveAll -> 그대로 반환
    willAnswer(invocation -> invocation.getArgument(0)).given(eventRepository).saveAll(anyList());

    // when
    List<Event> saved = service.createDefaultAnniversaries(match, userA, userB);

    // then
    then(eventRepository).should().saveAll(eventsCaptor.capture());
    List<Event> toSave = eventsCaptor.getValue();

    assertThat(toSave).hasSize(4);
    assertThat(saved).hasSize(4);

    // 타이틀 검증 (코드에서 "100일", "주년", "{닉네임} 생일")
    assertThat(toSave.stream().map(Event::getTitle))
        .containsExactlyInAnyOrder("100일", "주년", "A 생일", "B 생일");

    // 반복/알림/날짜 일부 검증
    Event hundred = toSave.stream().filter(e -> e.getTitle().equals("100일")).findFirst().orElseThrow();
    assertThat(hundred.getRepeatType()).isEqualTo(EventRepeatType.NONE);
    assertThat(hundred.getAlarmOption()).isEqualTo(EventAlarmOption.WEEK_BEFORE);
    assertThat(hundred.getEventAt()).isEqualTo(LocalDate.of(2025, 4, 11)); // 2025-01-01 + 100d

    Event anniv = toSave.stream().filter(e -> e.getTitle().equals("주년")).findFirst().orElseThrow();
    assertThat(anniv.getRepeatType()).isEqualTo(EventRepeatType.YEARLY);
    assertThat(anniv.getAlarmOption()).isEqualTo(EventAlarmOption.WEEK_BEFORE);
    assertThat(anniv.getEventAt()).isEqualTo(LocalDate.of(2025, 1, 1));

    Event aBday = toSave.stream().filter(e -> e.getTitle().equals("A 생일")).findFirst().orElseThrow();
    assertThat(aBday.getRepeatType()).isEqualTo(EventRepeatType.YEARLY);
    assertThat(aBday.getAlarmOption()).isEqualTo(EventAlarmOption.WEEK_BEFORE);
    assertThat(aBday.getEventAt()).isEqualTo(LocalDate.of(1990, 5, 10));

    Event bBday = toSave.stream().filter(e -> e.getTitle().equals("B 생일")).findFirst().orElseThrow();
    assertThat(bBday.getRepeatType()).isEqualTo(EventRepeatType.YEARLY);
    assertThat(bBday.getAlarmOption()).isEqualTo(EventAlarmOption.WEEK_BEFORE);
    assertThat(bBday.getEventAt()).isEqualTo(LocalDate.of(1992, 12, 3));
  }

  @Test
  void friendRelation_withBirthdays_createsTwoBirthdays_only() {
    // given
    Match match = mock(Match.class);
    given(match.getRelationType()).willReturn(RelationType.FRIEND);

    User userA = mock(User.class);
    given(userA.getNickname()).willReturn("A");
    given(userA.getBirthDate()).willReturn(LocalDate.of(1990, 5, 10));

    User userB = mock(User.class);
    given(userB.getNickname()).willReturn("B");
    given(userB.getBirthDate()).willReturn(LocalDate.of(1992, 12, 3));

    willAnswer(invocation -> invocation.getArgument(0)).given(eventRepository).saveAll(anyList());

    // when
    List<Event> saved = service.createDefaultAnniversaries(match, userA, userB);

    // then
    then(eventRepository).should().saveAll(eventsCaptor.capture());
    List<Event> toSave = eventsCaptor.getValue();

    assertThat(toSave).hasSize(2);
    assertThat(saved).hasSize(2);
    assertThat(toSave.stream().map(Event::getTitle))
        .containsExactlyInAnyOrder("A 생일", "B 생일");
  }

  @Test
  void coupleWithoutBirthdays_stillCreates100thAndAnniversary() {
    // given
    Match match = mock(Match.class);
    given(match.getRelationType()).willReturn(RelationType.COUPLE);
    given(match.getStartDate()).willReturn(LocalDateTime.of(2024, 2, 1, 0, 0));

    User userA = mock(User.class);
    given(userA.getBirthDate()).willReturn(null);

    User userB = mock(User.class);
    given(userB.getBirthDate()).willReturn(null);

    willAnswer(invocation -> invocation.getArgument(0)).given(eventRepository).saveAll(anyList());

    // when
    List<Event> saved = service.createDefaultAnniversaries(match, userA, userB);

    // then
    then(eventRepository).should().saveAll(eventsCaptor.capture());
    List<Event> toSave = eventsCaptor.getValue();

    assertThat(toSave).hasSize(2);
    assertThat(saved).hasSize(2);
    assertThat(toSave.stream().map(Event::getTitle)).containsExactlyInAnyOrder("100일", "주년");
  }
}
