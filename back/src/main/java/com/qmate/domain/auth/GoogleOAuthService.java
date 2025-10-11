package com.qmate.domain.auth;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class GoogleOAuthService {

  @Value("${app.oauth.google.client-id}")
  private String clientId;

  @Value("${app.oauth.google.client-secret}")
  private String clientSecret;

  @Value("${app.oauth.google.redirect-uri}")
  private String configuredRedirectUri;

  private final RestTemplate restTemplate = new RestTemplate();
  private final GoogleIdTokenVerifier verifier; // Bean 주입(그대로 사용)

  public GoogleTokenResponse exchange(String code, String redirectUri) {
    if (!configuredRedirectUri.equals(redirectUri)) {
      throw new IllegalArgumentException("redirect_uri mismatch");
    }

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

    MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
    form.add("code", code);
    form.add("client_id", clientId);
    form.add("client_secret", clientSecret);
    form.add("redirect_uri", redirectUri);
    form.add("grant_type", "authorization_code");

    HttpEntity<MultiValueMap<String, String>> req = new HttpEntity<>(form, headers);

    return restTemplate.postForObject(
        "https://oauth2.googleapis.com/token",
        req,
        GoogleTokenResponse.class
    );
  }

  public GoogleIdToken.Payload verifyIdToken(String idTokenString) {
    try {
      GoogleIdToken idToken = verifier.verify(idTokenString);
      if (idToken == null) throw new IllegalStateException("Invalid id_token");
      return idToken.getPayload();
    } catch (GeneralSecurityException | IOException e) {
      throw new IllegalStateException("id_token verify failed", e);
    }
  }

  @Getter @Setter
  public static class GoogleTokenResponse {
    private String access_token;
    private String id_token;
    private String refresh_token;
    private String token_type;
    private Long   expires_in;
    private String scope;
  }
}