package com.qmate.api.event;

import com.qmate.domain.event.model.request.EventCreateRequest;
import com.qmate.domain.event.model.response.EventResponse;
import com.qmate.domain.event.service.EventService;
import com.qmate.security.UserPrincipal;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

    URI location = ServletUriComponentsBuilder.fromCurrentContextPath()
        .path("/api/events/{id}")
        .buildAndExpand(response.getEventId())
        .toUri();

    return ResponseEntity.created(location).body(response);
  }
}
