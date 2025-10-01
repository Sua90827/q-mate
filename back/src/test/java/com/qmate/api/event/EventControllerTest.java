package com.qmate.api.event;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
import com.qmate.domain.event.model.response.EventResponse;
import com.qmate.domain.event.service.EventService;
import com.qmate.exception.custom.event.EventNotFoundException;
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
}
