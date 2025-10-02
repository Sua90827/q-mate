package com.qmate.domain.notification.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "push_subscription")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PushSubscription {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "push_subscription_id", nullable = false)
  private Long id;

  @Column(name = "user_id", nullable = false)
  private Long userId;

  @Column(name = "endpoint", nullable = false)
  private String endpoint;

  @Column(name = "endpoint_hash", nullable = false)
  private byte[] endpointHash;

  @Column(name = "key_p256dh", nullable = false)
  private String keyP256dh;

  @Column(name = "key_auth", nullable = false)
  private String keyAuth;

  @CreatedDate
  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  @LastModifiedDate
  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

  /**
   * 한 endpoint = 한 user 정책 하에서,
   * - 동일 endpoint의 소유권을 현재 사용자로 이전하거나(계정 전환)
   * - 키(p256dh/auth)를 최신화한다.
   */
  public void claimOrRefresh(Long newUserId, String newEndpoint, String newP256dh, String newAuth) {
    // endpoint가 바뀌면 해시 재계산
    if (!Objects.equals(this.endpoint, newEndpoint)) {
      this.endpoint = newEndpoint;
      this.endpointHash = sha256(newEndpoint);
    }
    // 소유권 이전 허용 (한 endpoint = 한 user 정책 하에서 전환)
    this.userId = newUserId;

    // 키는 매번 최신화
    this.keyP256dh = newP256dh;
    this.keyAuth = newAuth;
  }

  public static byte[] sha256(String value) {
    try {
      MessageDigest md = MessageDigest.getInstance("SHA-256");
      return md.digest(value.getBytes(StandardCharsets.UTF_8));
    } catch (NoSuchAlgorithmException e) {
      throw new IllegalStateException("SHA-256 not supported", e);
    }
  }
}
