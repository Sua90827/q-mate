package com.qmate.domain.auth;

import com.qmate.domain.user.User;
import com.qmate.domain.user.UserRepository;
import com.qmate.domain.user.UserSocialAccount;
import com.qmate.domain.user.UserSocialAccount.SocialProvider;
import com.qmate.domain.user.UserSocialAccountRepository;
import java.time.LocalDate;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SocialAccountService {
  private final UserRepository userRepository;
  private final UserSocialAccountRepository socialRepository;

  /**
   * Google 로그인용 upsert (google sub 기반)
   * @param googleSub        Google 고유 ID (sub)
   * @param email            구글에서 받은 이메일 (null 가능성 고려)
   * @param nickname         구글 display name (없으면 email/대체키 사용)
   */
  @Transactional
  public User upsertGoogleUser(String googleSub, String email, String nickname) {
    // 필요 시 이메일 정규화
    String normalizedEmail = (email != null) ? email.trim().toLowerCase() : null;
    String safeNickname = (nickname != null && !nickname.isBlank())
        ? nickname
        : (normalizedEmail != null ? normalizedEmail : "user_google_" + googleSub);

    return upsertSocialUser(
        SocialProvider.GOOGLE,
        googleSub,
        normalizedEmail,
        safeNickname,
        null
    );
  }

  /**
   * 소셜 로그인 정보로 사용자 upsert + 소셜계정 연결
   * 규칙:
   *  - provider+providerUserId 매칭되면 해당 user 반환
   *  - 없으면 email로 기존 user 찾고, 있으면 그 user에 소셜계정 연결
   *  - email로도 없으면 새 user 생성 후 소셜계정 연결
   */
  @Transactional
  public User upsertSocialUser(SocialProvider provider, String providerUserId,
      String email, String nickname, LocalDate birthDate) {
    //소셜 계정으로 바로 매칭
    Optional<UserSocialAccount> linked = socialRepository.findByProviderAndProviderUserId(provider, providerUserId);
    if (linked.isPresent()) {
      Long uid = linked.get().getUser().getId();
      return userRepository.findById(uid).orElseThrow();
    }

    //이메일로 기존 사용자 찾기
    User user = userRepository.findByEmail(email).orElseGet(() -> {
      //새 유저 생성
      return User.builder()
          .email(email)
          .passwordHash(null)
          .nickname(nickname != null && !nickname.isBlank() ? nickname : email)
          .birthDate(birthDate)
          .build();
    });

    //새 유저면 저장
    if (user.getId() == null) user = userRepository.save(user);

    //소셜 계정 연결 정보 저장
    UserSocialAccount acc = UserSocialAccount.builder()
        .user(user)
        .provider(provider)
        .providerUserId(providerUserId)
        .build();
    socialRepository.save(acc);

    return user;
  }

}
