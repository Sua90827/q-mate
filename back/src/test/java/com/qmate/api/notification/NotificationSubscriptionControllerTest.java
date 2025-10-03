package com.qmate.api.notification;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qmate.AuthTestUtils;
import com.qmate.SecuritySliceTestConfig;
import com.qmate.domain.notification.model.request.PushSubscriptionRegisterRequest;
import com.qmate.domain.notification.model.request.PushSubscriptionRegisterRequest.Keys;
import com.qmate.domain.notification.model.response.PushSubscriptionRegisterResponse;
import com.qmate.domain.notification.service.PushSubscriptionService;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = NotificationSubscriptionController.class)
@Import(SecuritySliceTestConfig.class)
class NotificationSubscriptionControllerTest {

  @Autowired
  MockMvc mvc;
  @Autowired
  ObjectMapper om;

  @MockitoBean
  PushSubscriptionService pushSubscriptionService;

  private PushSubscriptionRegisterRequest req() {
    return PushSubscriptionRegisterRequest.builder()
        .endpoint("https://fcm.googleapis.com/fcm/send/ABC")
        .keys(Keys.builder()
            .p256dh("B" + "x".repeat(87))
            .auth("A" + "y".repeat(23))
            .build())
        .build();
  }

  @Test
  @DisplayName("POST /api/notifications/subscriptions - 구독 업서트 성공")
  void upsert_ok() throws Exception {
    var request = req();
    var now = LocalDateTime.now();
    var resp = PushSubscriptionRegisterResponse.builder()
        .subscriptionId(123L).createdAt(now).updatedAt(now).build();

    given(pushSubscriptionService.upsert(eq(1L), any())).willReturn(resp);

    mvc.perform(post("/api/notifications/subscriptions")
            .with(AuthTestUtils.userPrincipal(1L))
            .contentType(MediaType.APPLICATION_JSON)
            .content(om.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.subscriptionId").value(123))
        .andExpect(jsonPath("$.createdAt").exists())
        .andExpect(jsonPath("$.updatedAt").exists());

    then(pushSubscriptionService).should().upsert(eq(1L), any(PushSubscriptionRegisterRequest.class));
  }

  @Test
  @DisplayName("DELETE /api/notifications/subscriptions/by-endpoint - 엔드포인트 기반 해지")
  void unsubscribe_by_endpoint() throws Exception {
    String endpoint = "https://fcm.googleapis.com/fcm/send/DELME";

    willDoNothing().given(pushSubscriptionService).unsubscribe(1L, endpoint);

    mvc.perform(delete("/api/notifications/subscriptions/by-endpoint")
            .with(AuthTestUtils.userPrincipal(1L))
            .param("endpoint", endpoint))
        .andExpect(status().isNoContent());

    then(pushSubscriptionService).should().unsubscribe(1L, endpoint);
  }

  @Test
  @DisplayName("DELETE /api/notifications/subscriptions/{id} - ID 기반 해지")
  void unsubscribe_by_id() throws Exception {
    long id = 999L;
    willDoNothing().given(pushSubscriptionService).unsubscribe(1L, id);

    mvc.perform(delete("/api/notifications/subscriptions/{id}", id)
            .with(AuthTestUtils.userPrincipal(1L)))
        .andExpect(status().isNoContent());

    then(pushSubscriptionService).should().unsubscribe(1L, id);
  }
}
