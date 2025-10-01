package com.qmate.domain.event.service;

import com.qmate.domain.event.entity.Event;
import com.qmate.domain.event.mapper.EventMapper;
import com.qmate.domain.event.model.request.EventCreateRequest;
import com.qmate.domain.event.model.response.EventResponse;
import com.qmate.domain.event.repository.EventRepository;
import com.qmate.domain.match.Match;
import com.qmate.domain.match.repository.MatchRepository;
import com.qmate.exception.custom.event.EventNotFoundException;
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
}
