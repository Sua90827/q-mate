package com.qmate.security.oauth;

import com.qmate.domain.auth.SocialAccountService;
import com.qmate.domain.user.User;
import com.qmate.domain.user.UserSocialAccount.SocialProvider;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

  private final SocialAccountService socialAccountService;

  @Override
  public OAuth2User loadUser(OAuth2UserRequest userRequest) {
    OAuth2User oAuth2User = super.loadUser(userRequest);
    Map<String, Object> attr = oAuth2User.getAttributes();

    String registrationId = userRequest.getClientRegistration().getRegistrationId(); // "google"
    if (!"google".equalsIgnoreCase(registrationId)) {
      throw new IllegalArgumentException("Unsupported provider: " + registrationId);
    }

    String sub   = (String) attr.get("sub");   // provider user id
    String email = (String) attr.get("email");
    String name  = (String) attr.get("name");

    if (sub == null) throw new IllegalStateException("Google response missing 'sub'");

    User user = socialAccountService.upsertSocialUser(SocialProvider.GOOGLE, sub, email, name);

    // 성공 핸들러에서 UserPrincipal로 통일할 것이므로, 여기서는 “임시” 사용자 정보 객체 리턴
    return new CustomOAuth2User(user.getId(), user.getEmail(), user.getRole().name(), attr);
  }
}