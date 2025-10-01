package com.qmate.api.event;

import com.qmate.domain.event.model.request.EventCreateRequest;
import com.qmate.domain.event.model.request.EventUpdateRequest;
import com.qmate.domain.event.model.response.EventResponse;
import com.qmate.domain.event.service.EventService;
import com.qmate.security.UserPrincipal;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class EventController {

  private final EventService eventService;

  /**
   * 일정 생성
   */
  @PostMapping("/matches/{matchId}/events")
  public ResponseEntity<EventResponse> createEvent(
      @PathVariable Long matchId,
      @AuthenticationPrincipal UserPrincipal principal,
      @Valid @RequestBody EventCreateRequest request
  ) {
    Long userId = principal.userId();

    EventResponse response = eventService.createEvent(matchId, userId, request);

    URI location = URI.create("/api/events/" + response.getEventId());

    return ResponseEntity.created(location).body(response);
  }

  /**
   * 일정 단건 조회
   */
  @GetMapping("/matches/{matchId}/events/{eventId}")
  public ResponseEntity<EventResponse> getEvent(
      @PathVariable Long matchId,
      @PathVariable Long eventId,
      @AuthenticationPrincipal UserPrincipal principal
  ) {
    Long userId = principal.userId();
    EventResponse response = eventService.getEvent(matchId, userId, eventId);
    return ResponseEntity.ok(response);
  }

  /**
   * 일정 수정
   * - 기념일 이벤트(anniversary=true)는 반복 설정(repeatType) 변경 불가
   */
  @PatchMapping("/matches/{matchId}/events/{eventId}")
  public ResponseEntity<EventResponse> updateEvent(
      @PathVariable Long matchId,
      @PathVariable Long eventId,
      @AuthenticationPrincipal UserPrincipal principal,
      @Valid @RequestBody EventUpdateRequest request
  ) {
    Long userId = principal.userId();
    EventResponse response = eventService.updateEvent(matchId, userId, eventId, request);
    return ResponseEntity.ok(response);
  }

  /**
   * 일정 삭제
   * - 기념일 이벤트(anniversary=true)는 삭제 불가
   */
  @DeleteMapping("/matches/{matchId}/events/{eventId}")
  public ResponseEntity<Void> deleteEvent(
      @PathVariable Long matchId,
      @PathVariable Long eventId,
      @AuthenticationPrincipal UserPrincipal principal
  ) {
    Long userId = principal.userId();
    eventService.deleteEvent(matchId, userId, eventId);
    return ResponseEntity.noContent().build();
  }
}
