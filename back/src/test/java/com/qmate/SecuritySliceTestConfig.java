package com.qmate;

import java.util.List;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@TestConfiguration
public class SecuritySliceTestConfig implements WebMvcConfigurer {
  @Bean
  SecurityFilterChain testFilterChain(HttpSecurity http) throws Exception {
    // 테스트에 불필요한 것만 끄고 최소 체인만
    return http.csrf(csrf -> csrf.disable()).build();
  }

  // @AuthenticationPrincipal이 동작하도록 리졸버 등록
  @Override
  public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
    resolvers.add(new org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver());
  }
}
