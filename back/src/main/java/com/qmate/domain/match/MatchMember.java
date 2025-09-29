package com.qmate.domain.match;

import com.qmate.domain.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "match_member")
public class MatchMember {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "match_member_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "match_id")
  private Match match;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private User user;

  @CreationTimestamp
  @Column(name = "joined_at", nullable = false, updatable = false)
  private LocalDateTime joinedAt;
  @UpdateTimestamp
  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

  private LocalDateTime lastAnsweredAt;
  @Builder.Default
  private boolean isAgreed = false;//기본값 미동의

  public void setMatch(Match match) {
    this.match = match;
  }
  //복구에 동의하는 메서드
  public void agreeToRestore(){
    this.isAgreed = true;
  }
  //동의 상태를 초기화하는 메서드
  public void resetAgreement(){
    this.isAgreed = false;
  }

  //정적 팩토리 메서드 추가
  public static MatchMember create(User user, Match match) {
    return MatchMember.builder()
        .user(user)
        .match(match)
        .lastAnsweredAt(LocalDateTime.now())
        .build();
  }

  // lastAnsweredAt 최신화 메서드
  public void updateLastAnsweredAt() {
    lastAnsweredAt = LocalDateTime.now();
  }
}
