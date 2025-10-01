package com.qmate.api.event;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qmate.AuthTestUtils;
import com.qmate.SecuritySliceTestConfig;
import com.qmate.domain.event.entity.EventAlarmOption;
import com.qmate.domain.event.entity.EventRepeatType;
import com.qmate.domain.event.model.request.EventCreateRequest;
import com.qmate.domain.event.model.request.EventUpdateRequest;
import com.qmate.domain.event.model.response.EventResponse;
import com.qmate.domain.event.service.EventService;
import com.qmate.exception.custom.event.EventDeletionNotAllowedException;
import com.qmate.exception.custom.event.EventNotFoundException;
import com.qmate.exception.custom.event.EventRepeatModificationNotAllowedException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = EventController.class)
@AutoConfigureMockMvc
@Import(SecuritySliceTestConfig.class)
class EventControllerTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @MockitoBean
  private EventService eventService;

  @Test
  @DisplayName("일정 생성 API - 201 Created + Location 헤더")
  void createEvent_created() throws Exception {
    long matchId = 1L;
    long userId = 99L;

    EventCreateRequest req = EventCreateRequest.builder()
        .title("제목")
        .description("설명")
        .eventAt(LocalDate.of(2025, 10, 9))
        .repeatType(EventRepeatType.NONE)
        .alarmOption(EventAlarmOption.WEEK_BEFORE)
        .build();

    EventResponse stub = EventResponse.builder()
        .eventId(10L)
        .title("제목")
        .description("설명")
        .eventAt(LocalDate.of(2025, 10, 9))
        .repeatType(EventRepeatType.NONE)
        .alarmOption(EventAlarmOption.WEEK_BEFORE)
        .anniversary(false)
        .createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .build();

    given(eventService.createEvent(anyLong(), anyLong(), any())).willReturn(stub);

    mockMvc.perform(post("/api/matches/{matchId}/events", matchId)
            .with(AuthTestUtils.userPrincipal(userId))
            .contentType(APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(req)))
        .andExpect(status().isCreated())
        .andExpect(header().string("Location", equalTo("/api/events/10")))
        .andExpect(jsonPath("$.eventId").value(10))
        .andExpect(jsonPath("$.title").value("제목"))
        .andExpect(jsonPath("$.repeatType").value("NONE"))
        .andExpect(jsonPath("$.alarmOption").value("WEEK_BEFORE"));
  }

  @Test
  @DisplayName("일정 생성 API - JSON 본문 enum 미스매치 시 400")
  void createEvent_enumMismatch_returns400() throws Exception {
    long matchId = 1L;

    // repeatType에 잘못된 값 주입 (문자열 직접 작성)
    String badJson = """
      {
        "title": "제목",
        "eventAt": "2025-10-09",
        "repeatType": "NOT_A_VALID_ENUM",
        "alarmOption": "WEEK_BEFORE"
      }
      """;

    mockMvc.perform(post("/api/matches/{matchId}/events", matchId)
            .with(AuthTestUtils.userPrincipal((99L)))
            .contentType(APPLICATION_JSON)
            .content(badJson))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errorCode").exists())
        .andExpect(jsonPath("$.message").exists())
        .andExpect(jsonPath("$.errors[0].field").exists());
  }

  @Test
  @DisplayName("상세 조회 API - 200 OK")
  void getEvent_ok() throws Exception {
    long matchId = 1L, eventId = 10L, userId = 99L;

    EventResponse stub = EventResponse.builder()
        .eventId(eventId)
        .title("제목")
        .description("설명")
        .eventAt(LocalDate.of(2025, 10, 9))
        .repeatType(EventRepeatType.NONE)
        .alarmOption(EventAlarmOption.WEEK_BEFORE)
        .anniversary(false)
        .createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .build();

    given(eventService.getEvent(matchId, userId, eventId)).willReturn(stub);

    mockMvc.perform(get("/api/matches/{matchId}/events/{eventId}", matchId, eventId)
            .with(AuthTestUtils.userPrincipal(userId))
            .accept(APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(APPLICATION_JSON))
        .andExpect(jsonPath("$.eventId").value((int) eventId))
        .andExpect(jsonPath("$.title").value("제목"))
        .andExpect(jsonPath("$.repeatType").value("NONE"))
        .andExpect(jsonPath("$.alarmOption").value("WEEK_BEFORE"));
  }

  @Test
  @DisplayName("상세 조회 API - 404 Not Found")
  void getEvent_notFound() throws Exception {
    long matchId = 1L, eventId = 10L, userId = 99L;

    given(eventService.getEvent(matchId, userId, eventId))
        .willThrow(new EventNotFoundException());

    mockMvc.perform(get("/api/matches/{matchId}/events/{eventId}", matchId, eventId)
            .with(AuthTestUtils.userPrincipal(userId))
            .accept(APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.errorCode").exists())
        .andExpect(jsonPath("$.message").exists());
  }

  @Test
  @DisplayName("일정 수정 API - 200 OK")
  void updateEvent_ok() throws Exception {
    long matchId = 1L, eventId = 10L, userId = 99L;

    EventUpdateRequest req = EventUpdateRequest.builder()
        .title("new-title")
        .repeatType(EventRepeatType.MONTHLY)
        .build();

    EventResponse stub = EventResponse.builder()
        .eventId(eventId)
        .title("new-title")
        .description("설명")
        .eventAt(LocalDate.of(2025,10,9))
        .repeatType(EventRepeatType.MONTHLY)
        .alarmOption(EventAlarmOption.WEEK_BEFORE)
        .anniversary(false)
        .createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .build();

    given(eventService.updateEvent(anyLong(), anyLong(), anyLong(), any())).willReturn(stub);

    mockMvc.perform(patch("/api/matches/{matchId}/events/{eventId}", matchId, eventId)
            .with(AuthTestUtils.userPrincipal(userId))
            .contentType(APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(req)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.eventId").value((int) eventId))
        .andExpect(jsonPath("$.title").value("new-title"))
        .andExpect(jsonPath("$.repeatType").value("MONTHLY"));
  }

  @Test
  @DisplayName("일정 수정 API - 기념일 repeatType 변경 시 409")
  void updateEvent_anniversary_repeat_forbidden_409() throws Exception {
    long matchId = 1L, eventId = 10L, userId = 99L;

    EventUpdateRequest req = EventUpdateRequest.builder()
        .repeatType(EventRepeatType.YEARLY)
        .build();

    given(eventService.updateEvent(anyLong(), anyLong(), anyLong(), any()))
        .willThrow(new EventRepeatModificationNotAllowedException());

    mockMvc.perform(patch("/api/matches/{matchId}/events/{eventId}", matchId, eventId)
            .with(AuthTestUtils.userPrincipal(userId))
            .contentType(APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(req)))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.errorCode").exists())
        .andExpect(jsonPath("$.message").exists());
  }

  @Test
  @DisplayName("일정 삭제 API - 204 No Content")
  void deleteEvent_noContent() throws Exception {
    long matchId = 1L, eventId = 10L, userId = 99L;

    mockMvc.perform(delete("/api/matches/{matchId}/events/{eventId}", matchId, eventId)
            .with(AuthTestUtils.userPrincipal(userId)))
        .andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("일정 삭제 API - 기념일 삭제 시 409")
  void deleteEvent_anniversary_forbidden_409() throws Exception {
    long matchId = 1L, eventId = 10L, userId = 99L;

    // 서비스에서 예외 던지도록
    willThrow(new EventDeletionNotAllowedException())
        .given(eventService)
        .deleteEvent(anyLong(), anyLong(), anyLong());

    mockMvc.perform(delete("/api/matches/{matchId}/events/{eventId}", matchId, eventId)
            .with(AuthTestUtils.userPrincipal(userId)))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.errorCode").exists())
        .andExpect(jsonPath("$.message").exists());
  }
}
