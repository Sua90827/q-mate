package com.qmate.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qmate.domain.auth.JwtService;
import com.qmate.exception.CommonErrorCode;
import com.qmate.exception.ErrorCode;
import com.qmate.exception.ErrorResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity //@PreAuthorize 작동
@RequiredArgsConstructor
public class SecurityConfig {

  private final JwtService jwtService;

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  private static final String[] SWAGGER_WHITELIST = {
      "/v3/api-docs/**",
      "/swagger-ui/**",
      "/swagger-ui.html"
  };

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http, ObjectMapper om) throws Exception {
    http
        .csrf(AbstractHttpConfigurer::disable)
        .formLogin(AbstractHttpConfigurer::disable)
        .httpBasic(AbstractHttpConfigurer::disable)
        // 세션 기반 인증 사용 안함
        .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .exceptionHandling(e -> e
            .authenticationEntryPoint((req, res, ex) -> {
              ErrorCode code = CommonErrorCode.unauthorized();
              res.setStatus(code.getHttpStatus().value());
              res.setContentType("application/json;charset=UTF-8");
              ErrorResponse body = new ErrorResponse(code.getCode(), code.getMessage());
              res.getWriter().write(om.writeValueAsString(body));
            })
            .accessDeniedHandler((req, res, ex) -> {
              ErrorCode code = CommonErrorCode.forbidden();
              res.setStatus(code.getHttpStatus().value());
              res.setContentType("application/json;charset=UTF-8");
              ErrorResponse body = new ErrorResponse(code.getCode(), code.getMessage());
              res.getWriter().write(om.writeValueAsString(body));
            })
        )
        .authorizeHttpRequests(auth -> auth
//            .requestMatchers("/auth/**").permitAll()
//            .requestMatchers(SWAGGER_WHITELIST).permitAll()
//            .requestMatchers("/api/admin/**").hasRole("ADMIN")
//            .anyRequest().authenticated()  // 나머지 모든 요청은 인증 필요
                .anyRequest().permitAll()
        )
        .addFilterBefore(new JwtAuthenticationFilter(jwtService), UsernamePasswordAuthenticationFilter.class);  // JWT 필터 추가

    return http.build();
  }
}