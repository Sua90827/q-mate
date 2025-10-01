package com.qmate.domain.event.service;

import com.qmate.domain.event.entity.Event;
import com.qmate.domain.event.mapper.EventMapper;
import com.qmate.domain.event.model.request.EventCreateRequest;
import com.qmate.domain.event.model.request.EventUpdateRequest;
import com.qmate.domain.event.model.response.EventResponse;
import com.qmate.domain.event.repository.EventRepository;
import com.qmate.domain.match.Match;
import com.qmate.domain.match.repository.MatchRepository;
import com.qmate.exception.custom.event.EventDeletionNotAllowedException;
import com.qmate.exception.custom.event.EventNotFoundException;
import com.qmate.exception.custom.event.EventRepeatModificationNotAllowedException;
import com.qmate.exception.custom.matchinstance.MatchNotFoundException;
import lombok.RequiredArgsConstructor;
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

  public void deleteEvent(Long matchId, Long userId, Long eventId) {
    Event event = eventRepository.findAuthorizedById(matchId, userId, eventId)
        .orElseThrow(EventNotFoundException::new);

    if (event.isAnniversary()) {
      throw new EventDeletionNotAllowedException();
    }

    eventRepository.delete(event);
  }
}
