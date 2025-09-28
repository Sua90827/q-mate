package com.qmate;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;

import com.qmate.security.UserPrincipal;
import java.util.List;
import lombok.NoArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public final class AuthTestUtils {

  public static RequestPostProcessor userPrincipal(long userId) {
    UserPrincipal principal = new UserPrincipal(userId, "user" + userId + "@test.com", "USER");
    var auth = new UsernamePasswordAuthenticationToken(
        principal, null, List.of(new SimpleGrantedAuthority("ROLE_USER")));
    return authentication(auth);
  }

  public static RequestPostProcessor adminPrincipal(long adminId) {
    UserPrincipal principal = new UserPrincipal(adminId, "admin" + adminId + "@test.com", "ADMIN");
    var auth = new UsernamePasswordAuthenticationToken(
        principal, null, List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
    return authentication(auth);
  }
}