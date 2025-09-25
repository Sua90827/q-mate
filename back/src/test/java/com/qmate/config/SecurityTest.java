package com.qmate.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureMockMvc
@SpringBootTest
class SecurityTest {

  @Autowired
  MockMvc mvc;

  @Test
  void authPaths_are_permitAll_for_anonymous() throws Exception {
    mvc.perform(post("/auth/login"))
        .andExpect(result -> {
          int code = result.getResponse().getStatus();
          // 내부 비즈니스 오류로 5xx/4xx가 나와도, 보안 관점에서는 401/403만 아니면 통과
          assertThat(code).isNotIn(401, 403);
        });
  }

  @Test
  void adminPaths_require_authentication() throws Exception {
    mvc.perform(get("/api/admin/question-categories"))
        .andExpect(status().isUnauthorized());
  }

  @Test
  @WithMockUser(roles = "USER")
  void adminPaths_forbid_user_role() throws Exception {
    mvc.perform(get("/api/admin/question-categories"))
        .andExpect(status().isForbidden());
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void adminPaths_allow_admin_role() throws Exception {
    mvc.perform(get("/api/admin/question-categories"))
        .andExpect(result -> {
          int code = result.getResponse().getStatus();
          // 내부 비즈니스 오류로 5xx/4xx가 나와도, 보안 관점에서는 401/403만 아니면 통과
          assertThat(code).isNotIn(401, 403);
        });
  }
}
