package com.qmate.domain.auth;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class NaverOAuthService {

  @Value("${app.oauth.naver.client-id}")
  private String clientId;

  @Value("${app.oauth.naver.client-secret}")
  private String clientSecret;

  @Value("${app.oauth.naver.redirect-uri}")
  private String configuredRedirectUri;

  private final RestTemplate restTemplate = new RestTemplate();

  /** code → token 교환 */
  public NaverTokenResponse exchange(String code, String state, String redirectUri) {
    if (!configuredRedirectUri.equals(redirectUri)) {
      throw new IllegalArgumentException("redirect_uri mismatch");
    }

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

    MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
    form.add("grant_type", "authorization_code");
    form.add("client_id", clientId);
    form.add("client_secret", clientSecret);
    form.add("code", code);
    form.add("state", state != null ? state : "");

    HttpEntity<MultiValueMap<String, String>> req = new HttpEntity<>(form, headers);

    return restTemplate.postForObject(
        "https://nid.naver.com/oauth2.0/token",
        req,
        NaverTokenResponse.class
    );
  }

  /** access_token → 사용자 프로필 조회 */
  public NaverUserProfile fetchProfile(String accessToken) {
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(accessToken);

    ResponseEntity<NaverUserInfoResponse> res = restTemplate.exchange(
        "https://openapi.naver.com/v1/nid/me",
        HttpMethod.GET,
        new HttpEntity<>(headers),
        NaverUserInfoResponse.class
    );

    if (!res.getStatusCode().is2xxSuccessful() || res.getBody() == null) {
      throw new IllegalStateException("Failed to fetch Naver profile");
    }
    NaverUserInfoResponse body = res.getBody();
    if (!"00".equals(body.getResultcode())) {
      throw new IllegalStateException("Naver profile error: " + body.getMessage());
    }

    NaverUserProfile profile = body.getResponse();
    if (profile.getEmail() == null || profile.getEmail().isBlank()) {
      throw new IllegalStateException("Naver profile has no email (but email is required).");
    }
    return profile;
  }

  // ===== DTOs =====
  @Getter @Setter
  public static class NaverTokenResponse {
    private String access_token;
    private String refresh_token;
    private String token_type;     // e.g., "bearer"
    private String expires_in;     // seconds as string
    private String error;
    private String error_description;
    private String id_token;       // (OIDC scope일 때만)
  }

  @Getter @Setter
  public static class NaverUserInfoResponse {
    private String resultcode; // "00" 이면 성공
    private String message;
    private NaverUserProfile response;
  }

  @Getter @Setter
  public static class NaverUserProfile {
    private String id;        // 고유 식별자 (필수)
    private String email;     // 동의/인증 시
    private String name;      // 동의 시
    private String nickname;  // 동의 시
    private String birthday;  // "MM-DD" (scope: birthday)
    private String birthyear; // "YYYY" (scope: birthyear)
    private String mobile;    // 동의 시
    // 필요 시 필드 추가
  }
}