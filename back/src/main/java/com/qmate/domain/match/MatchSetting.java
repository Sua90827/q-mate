package com.qmate.domain.match;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "match_setting")
public class MatchSetting {

  @Id
  @Column(name = "match_id")
  private Long id;

  @OneToOne(fetch = FetchType.LAZY)
  @MapsId
  @JoinColumn(name = "match_id")
  private Match match;

  @Column(name = "daily_question_hour", nullable = false)
  private int dailyQuestionHour = 12;

  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

  // Match와 함께 처음 생성될 때 사용하는 생성자
  public MatchSetting(Match match) {
    this.match = match;
    this.id = match.getId();
  }

  // 질문 시간을 업데이트하는 전용 메서드
  public void updateDailyQuestionHour(int hour) {
    this.dailyQuestionHour = hour;
  }
}
