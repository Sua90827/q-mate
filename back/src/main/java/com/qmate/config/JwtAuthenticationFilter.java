package com.qmate.config;

import com.qmate.domain.auth.JwtService;
import com.qmate.security.context.UserContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
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
    try{
      String token = getJwtFromRequest(request);
      if(token != null) {
        Authentication auth = jwtService.getAuthentication(token); // 유효하지 않으면 null
        if(auth != null) {
          SecurityContextHolder.getContext().setAuthentication(auth);

          // UserContext 채우기 (principal을 userId 문자열로 저장했다고 가정)
          Object principal = auth.getPrincipal();
          String userId = (principal != null) ? principal.toString() : null;

          // 권한에서 ROLE_ 접두사 제거해 원래 롤 문자열로 세팅
          String role = auth.getAuthorities().stream()
              .findFirst()
              .map(a -> a.getAuthority().replaceFirst("^ROLE_", ""))
              .orElse(null);

          //details에 실어둔 email 꺼내기
          Object details = auth.getDetails();
          String email = (details instanceof String s)? s : null;

          if(userId != null) UserContext.setUserId(userId);
          if(role != null) UserContext.setUserRole(role);
          if(email != null) UserContext.setUserEmail(email);
        }
      }
      chain.doFilter(request, response);
    }finally{
      // 스레드 재사용 대비 클리어 (매우 중요)
      UserContext.clear();
    }
  }

  private String getJwtFromRequest(HttpServletRequest request) {
    String bearer = request.getHeader("Authorization");
    if(bearer != null && bearer.startsWith("Bearer ")) {
      return bearer.substring(7);
    }
    return null;
  }
}