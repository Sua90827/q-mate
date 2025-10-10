package com.qmate.config;

import com.qmate.domain.auth.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtService jwtService;

  public JwtAuthenticationFilter(JwtService jwtService) {
    this.jwtService = jwtService;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
    throws ServletException, IOException {
    if (SecurityContextHolder.getContext().getAuthentication() == null) {
      String token = resolveToken(request);
      if (token != null) {
        try {
          Authentication auth = jwtService.getAuthentication(token); // 유효하지 않으면 null
          if (auth != null) {
            SecurityContextHolder.getContext().setAuthentication(auth);
          }
        } catch (Exception e) {
          log.debug("JWT parse/validate failed: {}", e.getMessage());
        }
      }
    }
    chain.doFilter(request, response);
  }

  private String resolveToken(HttpServletRequest req) {
    // 1) Authorization 헤더 Bearer ...
    String h = req.getHeader("Authorization");
    if (h != null && h.startsWith("Bearer ")) return h.substring(7);

    // 2) 쿠키 ACCESS_TOKEN
    Cookie[] cookies = req.getCookies();
    if (cookies != null) {
      for (Cookie c : cookies) {
        if ("ACCESS_TOKEN".equals(c.getName())) return c.getValue();
      }
    }
    return null;
  }
}