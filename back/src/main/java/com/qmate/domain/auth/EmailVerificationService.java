package com.qmate.domain.auth;

import com.qmate.domain.mail.MailSender;
import com.qmate.exception.custom.auth.AttemptsLimitExceededException;
import com.qmate.exception.custom.auth.ResendCooldownException;
import com.qmate.exception.custom.auth.VerificationInvalidOrExpiredException;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {
  private final StringRedisTemplate redis;
  private final MailSender mail;
  private static final SecureRandom RND = new SecureRandom();

  private static final Duration CODE_TTL = Duration.ofMinutes(10);//인증코드 유효기간 10분
  private static final Duration RESEND_COOLDOWN = Duration.ofSeconds(15);//재전송 쿨다운 15초
  private static final Duration OK_TOKEN_TTL = Duration.ofMinutes(3);//OK token 유효기간 3분
  private static final int MAX_ATTEMPTS = 5;//최대 시도 횟수 5회

  //Redis 키 규칙 메서드
  private String codeKey(String p, String e){
    return "EV:"+p+":"+e;
  }
  private String attemptKey(String p, String e){
    return "EV_ATTEMPT:"+p+":"+e;
  }
  private String resendKey(String p, String e){
    return "EV_RESEND:"+p+":"+e;
  }
  private String okTokenKey(String token){
    return "EV_OK_TOKEN:"+token;
  }

  public void sendCode(String email, String purpose) {
    if (Boolean.TRUE.equals(redis.hasKey(resendKey(purpose, email)))) {
      throw new ResendCooldownException();
    }
    String code = String.format("%06d", RND.nextInt(1_000_000));//0~999,999 범위 난수 생성 후 6자리 0패딩 문자열로 포맷
    redis.opsForValue().set(codeKey(purpose, email), code, CODE_TTL);
    redis.opsForValue().set(attemptKey(purpose, email), "0", CODE_TTL);
    redis.opsForValue().set(resendKey(purpose, email), "1", RESEND_COOLDOWN);

    mail.send(email, "[Qmate] 이메일 인증코드",
        "인증코드: " + code + "\n유효시간: " + CODE_TTL.toMinutes() + "분\n목적: " + purpose);
  }

  public String verifyAndIssueToken(String email, String purpose, String inputCode) {
    String stored = redis.opsForValue().get(codeKey(purpose, email));
    if (!StringUtils.hasText(stored)) throw new VerificationInvalidOrExpiredException();

    long attempts = increment(attemptKey(purpose, email), CODE_TTL);
    if (attempts > MAX_ATTEMPTS) {
      deleteAll(purpose, email);
      throw new AttemptsLimitExceededException();
    }

    if (!stored.equals(inputCode)) throw new VerificationInvalidOrExpiredException();

    //성공-----------------
    deleteAll(purpose, email);

    // 가입 직전 확인용 토큰(짧은 TTL)
    String token = UUID.randomUUID().toString();
    redis.opsForValue().set(okTokenKey(token), purpose+":"+email, OK_TOKEN_TTL);
    return token;
  }

  public boolean consumeOkToken(String okToken, String expectedPurpose, String expectedEmail) {
    String tokenKey = okTokenKey(okToken);
    String storedTokenBinding = redis.opsForValue().get(tokenKey);
    if (storedTokenBinding == null) return false;

    String normalizedEmail = normalizeEmail(expectedEmail);
    String expectedTokenBinding = expectedPurpose + ":" + normalizedEmail;

    boolean isMatch = expectedTokenBinding.equals(storedTokenBinding);
    if (isMatch) {
      redis.delete(tokenKey);
    }
    return isMatch;
  }

  private String normalizeEmail(String email) {
    return (email == null)? null : email.trim().toLowerCase();
  }

  private long increment(String key, Duration ttl) {
    Long v = redis.opsForValue().increment(key);
    if (v != null && v == 1L) redis.expire(key, ttl);
    return v == null ? 0L : v;
  }

  private void deleteAll(String purpose, String email) {
    redis.delete(codeKey(purpose, email));
    redis.delete(attemptKey(purpose, email));
    redis.delete(resendKey(purpose, email));
  }
}
