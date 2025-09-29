package com.qmate.domain.user;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserSocialAccountRepository extends JpaRepository<UserSocialAccount, Long> {
  Optional<UserSocialAccount> findByProviderAndProviderUserId(
      UserSocialAccount.SocialProvider provider, String providerUserId);
}
