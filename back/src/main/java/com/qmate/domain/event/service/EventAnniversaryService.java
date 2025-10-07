package com.qmate.domain.event.service;

import com.qmate.domain.event.entity.Event;
import com.qmate.domain.event.entity.EventAlarmOption;
import com.qmate.domain.event.entity.EventRepeatType;
import com.qmate.domain.event.repository.EventRepository;
import com.qmate.domain.match.Match;
import com.qmate.domain.match.RelationType;
import com.qmate.domain.user.User;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventAnniversaryService {

  private static final ZoneId ZONE_KST = ZoneId.of("Asia/Seoul");

  private final EventRepository eventRepository;

  public List<Event> createDefaultAnniversaries(Match match, User userA, User userB) {
    List<Event> toSave = new ArrayList<>();

    // 1) 100일 (startDate 가 있을 때만)
    if (match.getRelationType().equals(RelationType.COUPLE) && match.getStartDate() != null) {
      LocalDate hundredth = match.getStartDate().atZone(ZONE_KST).toLocalDate().plusDays(100);
      toSave.add(
          Event.builder()
              .match(match)
              .title("100일")
              .description(null)
              .eventAt(hundredth)
              .repeatType(EventRepeatType.NONE)
              .alarmOption(EventAlarmOption.WEEK_BEFORE)
              .anniversary(true)
              .build()
      );

      // 2) N주년 (매년 반복)
      LocalDate baseAnniv = match.getStartDate().atZone(ZONE_KST).toLocalDate();
      toSave.add(
          Event.builder()
              .match(match)
              .title("주년")
              .description(null)
              .eventAt(baseAnniv)
              .repeatType(EventRepeatType.YEARLY)
              .alarmOption(EventAlarmOption.WEEK_BEFORE)
              .anniversary(true)
              .build()
      );
    }

    // 3) 유저A 생일 (매년 반복)
    if (userA.getBirthDate() != null) {
      LocalDate baseBirthdayA = userA.getBirthDate(); // 연도는 무시, 월/일 기준으로 반복
      toSave.add(
          Event.builder()
              .match(match)
              .title(userA.getNickname() + " 생일")
              .description(null)
              .eventAt(baseBirthdayA)
              .repeatType(EventRepeatType.YEARLY)
              .alarmOption(EventAlarmOption.WEEK_BEFORE)
              .anniversary(true)
              .build()
      );
    }

    // 4) 유저B 생일 (매년 반복)
    if (userB.getBirthDate() != null) {
      LocalDate baseBirthdayB = userB.getBirthDate();
      toSave.add(
          Event.builder()
              .match(match)
              .title(userB.getNickname() + " 생일")
              .description(null)
              .eventAt(baseBirthdayB)
              .repeatType(EventRepeatType.YEARLY)
              .alarmOption(EventAlarmOption.WEEK_BEFORE)
              .anniversary(true)
              .build()
      );
    }

    if (toSave.isEmpty()) {
      return List.of();
    }
    return eventRepository.saveAll(toSave);
  }
}
