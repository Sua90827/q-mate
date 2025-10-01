package com.qmate.security.oauth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qmate.exception.CommonErrorCode;
import com.qmate.exception.ErrorCode;
import com.qmate.exception.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2FailureHandler implements AuthenticationFailureHandler {
  private final ObjectMapper objectMapper;

  @Override
  public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
      AuthenticationException exception) throws IOException {
    log.warn("OAuth2 로그인 실패: {}", exception.getMessage());

    ErrorCode ec = CommonErrorCode.unauthorized(); // 401 / C_401 같은 공통 코드 사용
    ErrorResponse body = new ErrorResponse(ec.getCode(), ec.getMessage());

    response.setStatus(ec.getHttpStatus().value());
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setCharacterEncoding("UTF-8");
    objectMapper.writeValue(response.getWriter(), body);
    response.getWriter().flush();
  }
}