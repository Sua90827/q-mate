package com.qmate.domain.pet;

import com.qmate.domain.match.Match;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "pet_exp_log")
public class PetExpLog {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "pet_exp_log_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "match_id", nullable = false)
  private Match match;

  @Column(nullable = false)
  private int delta; // 경험치 변화량 (예: +10, -5)

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private ExpReason reason;

  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @Builder
  private PetExpLog(Match match, int delta, ExpReason reason) {
    this.match = match;
    this.delta = delta;
    this.reason = reason;
  }

}
