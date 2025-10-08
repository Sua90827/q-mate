package com.qmate.api.notification;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qmate.domain.notification.model.response.PushSettingResponse;
import com.qmate.domain.notification.service.PushSettingService;
import com.qmate.SecuritySliceTestConfig;
import com.qmate.AuthTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@WebMvcTest(NotificationSettingController.class)
@Import(SecuritySliceTestConfig.class)
class PushSettingControllerTest {

  @Autowired MockMvc mvc;
  @Autowired ObjectMapper om;

  @MockitoBean
  PushSettingService pushSettingService;

  private static final Long PRINCIPAL = 1L;

  @Test
  @DisplayName("GET /api/notifications/settings: 현재 설정 반환")
  void get_returns_current_setting() throws Exception {
    given(pushSettingService.get(1L)).willReturn(new PushSettingResponse(true));

    mvc.perform(get("/api/notifications/settings")
            .with(AuthTestUtils.userPrincipal(PRINCIPAL)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.pushEnabled").value(true));
  }

  @Test
  @DisplayName("PATCH /api/notifications/settings: 설정 변경 후 결과 반환")
  void patch_updates_and_returns() throws Exception {
    given(pushSettingService.update(1L, false)).willReturn(new PushSettingResponse(false));

    mvc.perform(patch("/api/notifications/settings")
            .with(AuthTestUtils.userPrincipal(PRINCIPAL))
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"pushEnabled\": false}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.pushEnabled").value(false));
  }

  @Test
  @DisplayName("PATCH 본문 유효성 검증 - pushEnabled 누락 시 400")
  void patch_validation_missing_body() throws Exception {
    mvc.perform(patch("/api/notifications/settings")
            .with(AuthTestUtils.userPrincipal(PRINCIPAL))
            .contentType(MediaType.APPLICATION_JSON)
            .content("{}"))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("PATCH 본문 유효성 검증 - pushEnabled boolean 타입이 아닐 시 400")
  void patch_validation_type_mismatch_body() throws Exception {
    String json = """
        { "pushEnabled": "notBoolean" }
        """;
    mvc.perform(patch("/api/notifications/settings")
            .with(AuthTestUtils.userPrincipal(PRINCIPAL))
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
        .andExpect(status().isBadRequest());
  }
}
