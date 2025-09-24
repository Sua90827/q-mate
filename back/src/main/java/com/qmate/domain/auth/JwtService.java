package com.qmate.domain.auth;

import com.qmate.security.UserPrincipal;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Date;
import javax.crypto.SecretKey;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;


@Component
public class JwtService {

  @Value("${security.jwt.secret}")
  private String secret;

  @Value("${security.jwt.access-ttl-seconds:900}")
  private long accessTtlSeconds; // 15분(기본)

  @Value("${security.jwt.refresh-ttl-seconds:1209600}")
  private long refreshTtlSeconds; // 14일(기본)

  private SecretKey key() {
    return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
  }

  /** 액세스/리프레시 토큰 발급 */
  public TokenPair issue(Long userId, String role,String email) {
    long now = System.currentTimeMillis();
    Date issuedAt = new Date(now);
    Date accessExp = new Date(now + Duration.ofSeconds(accessTtlSeconds).toMillis());
    Date refreshExp = new Date(now + Duration.ofSeconds(refreshTtlSeconds).toMillis());

    // role 클레임은 접두사 없이 저장 ("USER", "ADMIN")
    String access = Jwts.builder()
        .setSubject(String.valueOf(userId))
        .claim("role", role)
        .claim("email", email)
        .claim("typ", "access")
        .setIssuedAt(issuedAt)
        .setExpiration(accessExp)
        .signWith(key(), SignatureAlgorithm.HS256)
        .compact();

    String refresh = Jwts.builder()
        .setSubject(String.valueOf(userId))
        .claim("role", role)
        .claim("typ", "refresh")
        .setIssuedAt(issuedAt)
        .setExpiration(refreshExp)
        .signWith(key(), SignatureAlgorithm.HS256)
        .compact();

    return new TokenPair(access, refresh, accessTtlSeconds, refreshTtlSeconds);
  }

  /** 토큰 검증 + Spring Security Authentication 생성 */
  public Authentication getAuthentication(String bearerToken) {
    try {
      Jws<Claims> jws = Jwts.parserBuilder().setSigningKey(key()).build().parseClaimsJws(bearerToken);
      Claims c = jws.getBody();
      Long userId = Long.valueOf(c.getSubject());
      String role = c.get("role", String.class); // "USER"/"ADMIN" 등 (접두사 없음)
      String email = c.get("email", String.class); // 없으면 null

      var principal   = new UserPrincipal(userId, email, role);
      var authorities = java.util.List.of(new SimpleGrantedAuthority("ROLE_" + role));
      return new UsernamePasswordAuthenticationToken(principal, null, authorities);
    } catch (Exception e) {
      return null; // 유효하지 않음 → 인증 미설정
    }
  }

  @Getter
  public static class TokenPair {
    private final String accessToken;
    private final String refreshToken;
    private final long accessTokenTtlSeconds;
    private final long refreshTokenTtlSeconds;

    public TokenPair(String accessToken, String refreshToken, long aTtl, long rTtl) {
      this.accessToken = accessToken;
      this.refreshToken = refreshToken;
      this.accessTokenTtlSeconds = aTtl;
      this.refreshTokenTtlSeconds = rTtl;
    }
  }
}