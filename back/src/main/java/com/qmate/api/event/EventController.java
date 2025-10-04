package com.qmate.api.event;

import com.qmate.common.constants.event.EventConstants;
import com.qmate.domain.event.entity.EventRepeatType;
import com.qmate.domain.event.model.request.EventCreateRequest;
import com.qmate.domain.event.model.request.EventUpdateRequest;
import com.qmate.domain.event.model.response.CalendarMonthResponse;
import com.qmate.domain.event.model.response.EventResponse;
import com.qmate.domain.event.service.EventService;
import com.qmate.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "Event", description = "일정 API")
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("/api")
public class EventController {

  private final EventService eventService;

  /**
   * 일정 생성
   */
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(
      summary = "일정 생성",
      description = EventConstants.CREATE_MD,
      parameters = {
          @Parameter(name = "matchId", description = "매치 ID")
      }
  )
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
  @Operation(
      summary = "일정 단건 조회",
      description = EventConstants.GET_DETAIL_MD,
      parameters = {
          @Parameter(name = "matchId", description = "매치 ID"),
          @Parameter(name = "eventId", description = "일정 ID")
      }
  )
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
  @Operation(
      summary = "일정 수정",
      description = EventConstants.UPDATE_MD,
      parameters = {
          @Parameter(name = "matchId", description = "매치 ID"),
          @Parameter(name = "eventId", description = "일정 ID")
      }
  )
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
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(
      summary = "일정 삭제",
      description = EventConstants.DELETE_MD,
      parameters = {
          @Parameter(name = "matchId", description = "매치 ID"),
          @Parameter(name = "eventId", description = "일정 ID")
      }
  )
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

  /**
   * 일정 리스트 조회
   */
  @Operation(
      summary = "일정 리스트 조회(반복 전개 포함)",
      description = EventConstants.LIST_MD,
      parameters = {
          @Parameter(name = "matchId", description = "매치 ID"),
          @Parameter(name = "from", description = "조회 시작 날짜(yyyy-MM-dd)", required = true),
          @Parameter(name = "to", description = "조회 종료 날짜(yyyy-MM-dd)", required = true),
          @Parameter(name = "repeatType", description = "반복 유형 필터"),
          @Parameter(name = "anniversary", description = "기념일 여부 필터"),
          @Parameter(name = "size", description = "페이지 크기 (최대 100)"),
          @Parameter(name = "sort", description = "사용하지 않음")
      }
  )
  @GetMapping("/matches/{matchId}/events")
  public ResponseEntity<Page<EventResponse>> listEvents(
      @PathVariable Long matchId,
      @AuthenticationPrincipal UserPrincipal principal,
      @RequestParam @DateTimeFormat(iso = ISO.DATE) LocalDate from,
      @RequestParam @DateTimeFormat(iso = ISO.DATE) LocalDate to,
      @RequestParam(required = false) EventRepeatType repeatType,
      @RequestParam(required = false) Boolean anniversary,
      @PageableDefault(page = 0, size = 20)
      @ParameterObject Pageable pageable
  ) {
    // size 상한 100
    int size = Math.min(pageable.getPageSize(), 100);
    Pageable capped = PageRequest.of(pageable.getPageNumber(), size, pageable.getSort());
    return ResponseEntity.ok(eventService.listEvents(matchId, principal.userId(), from, to, repeatType, anniversary, capped));
  }

  /**
   * 캘린더 월 조회
   */
  @Operation(
      summary = "캘린더 월 조회",
      description = EventConstants.CALENDAR_MD,
      parameters = {
          @Parameter(name = "matchId", description = "매치 ID"),
          @Parameter(name = "from", description = "조회 시작 날짜(yyyy-MM-dd)", required = true),
          @Parameter(name = "to", description = "조회 종료 날짜(yyyy-MM-dd)", required = true)
      }
  )
  @GetMapping("/matches/{matchId}/events/calendar")
  public CalendarMonthResponse getCalendar(
      @PathVariable Long matchId,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
      @AuthenticationPrincipal UserPrincipal principal
  ) {
    Long userId = principal.userId();
    return eventService.getCalendarMonth(matchId, userId, from, to);
  }
}
