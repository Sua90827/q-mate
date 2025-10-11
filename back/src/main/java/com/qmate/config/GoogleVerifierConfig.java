package com.qmate.config;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import java.util.Collections;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
//import com.google.api.client.json.jackson2.JacksonFactory;

@Configuration
public class GoogleVerifierConfig {

  @Value("${app.oauth.google.client-id}")
  private String clientId;

  @Bean
  public GoogleIdTokenVerifier googleIdTokenVerifier() {
    return new GoogleIdTokenVerifier.Builder(
        new NetHttpTransport(),
        GsonFactory.getDefaultInstance()
    )
        .setAudience(Collections.singletonList(clientId))
        .build();
  }
}