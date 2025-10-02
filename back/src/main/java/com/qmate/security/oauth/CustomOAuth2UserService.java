package com.qmate.security.oauth;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qmate.domain.auth.SocialAccountService;
import com.qmate.domain.user.User;
import com.qmate.domain.user.UserSocialAccount.SocialProvider;
import com.qmate.exception.custom.auth.OAuthResponseMissingIdException;
import com.qmate.exception.custom.auth.OAuthResponseParseException;
import java.time.LocalDate;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
  //네이버만 거침
  //구글(oidc)은 여기 안 타고 기본 OIDC 경로로 가서 SuccessHandler에서 처리됨

  private final SocialAccountService socialAccountService;

  @Override
  public OAuth2User loadUser(OAuth2UserRequest userRequest) {
    OAuth2User oAuth2User = super.loadUser(userRequest);
    Map<String, Object> attr = oAuth2User.getAttributes();

    String registrationId = userRequest.getClientRegistration().getRegistrationId(); // "naver", "google"

    if ("naver".equalsIgnoreCase(registrationId)) {
      Map<String, Object> resp = (Map<String, Object>) attr.getOrDefault("response", attr);

      try {
        log.debug("NAVER attrs = {}", new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(resp));
      } catch (JsonProcessingException e) {
        throw new OAuthResponseParseException();
      }

      String providerUserId = String.valueOf(resp.get("id"));
      String email = (String) resp.get("email");
      String name  = (String) resp.getOrDefault("name", email);
      LocalDate birthDate = parseNaverBirth(resp);

      if (providerUserId == null) {
        throw new OAuthResponseMissingIdException();
      }

      User user = socialAccountService.upsertSocialUser(
          SocialProvider.NAVER, providerUserId, email, name, birthDate
      );

      //Principal 형태로 리턴 → 성공 핸들러는 그대로 동작
      return new CustomOAuth2User(user.getId(), user.getEmail(), user.getRole().name(), resp);
    }

    return oAuth2User;
  }

  private LocalDate parseNaverBirth(Map<String, Object> resp) {
    String yyyy = (String) resp.get("birthyear");
    String mmdd = (String) resp.get("birthday");
    if (yyyy != null && mmdd != null && mmdd.matches("\\d{2}-\\d{2}")) {
      return LocalDate.parse(yyyy + "-" + mmdd);
    }
    return null;//하나라도 없으면
  }
}