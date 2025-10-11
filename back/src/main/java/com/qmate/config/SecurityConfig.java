package com.qmate.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qmate.domain.auth.JwtService;
import com.qmate.security.oauth.CustomOAuth2UserService;
import com.qmate.security.oauth.OAuth2FailureHandler;
import com.qmate.security.oauth.OAuth2SuccessHandler;
import com.qmate.exception.CommonErrorCode;
import com.qmate.exception.ErrorCode;
import com.qmate.exception.ErrorResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity //@PreAuthorize 작동
@RequiredArgsConstructor
public class SecurityConfig {

  private final JwtService jwtService;
  private final CustomOAuth2UserService customOAuth2UserService;
  private final OAuth2SuccessHandler oAuth2SuccessHandler;
  private final OAuth2FailureHandler oAuth2FailureHandler;

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
        .authorizeHttpRequests(authz -> authz
            .requestMatchers("/auth/**", "/oauth2/**", "/login/**",
                "/login/oauth2/**", "/oauth2/authorization/**", "/actuator/**", "/auth/exchange", "/auth/google/exchange").permitAll()

            .requestMatchers(SWAGGER_WHITELIST).permitAll()
            .requestMatchers("/api/admin/**").hasRole("ADMIN")
            .anyRequest().authenticated()  // 나머지 모든 요청은 인증 필요
        )
        .oauth2Login(oauth -> oauth
            .userInfoEndpoint(u -> u.userService(customOAuth2UserService))
            .successHandler(oAuth2SuccessHandler)
            .failureHandler(oAuth2FailureHandler)
        )
        .addFilterBefore(new JwtAuthenticationFilter(jwtService), UsernamePasswordAuthenticationFilter.class);  // JWT 필터 추가

    return http.build();
  }
  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    var cfg = new CorsConfiguration();
    cfg.setAllowedOrigins(List.of("https://q-mate.vercel.app"));
    cfg.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS"));
    cfg.setAllowedHeaders(List.of("*"));
    cfg.setAllowCredentials(true); //쿠키 주고받기
    var src = new UrlBasedCorsConfigurationSource();
    src.registerCorsConfiguration("/**", cfg);
    return src;
  }
}