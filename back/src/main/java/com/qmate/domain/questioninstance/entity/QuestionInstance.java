package com.qmate.domain.questioninstance.entity;

import com.qmate.domain.match.Match;
import com.qmate.domain.question.entity.CustomQuestion;
import com.qmate.domain.question.entity.Question;
import com.qmate.exception.custom.questioninstance.QuestionInstanceInvalidXorException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "question_instance")
@EntityListeners(AuditingEntityListener.class)
public class QuestionInstance {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "question_instance_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "match_id", nullable = false)
  private Match match;

  // 관리자 질문 또는 커스텀 질문 중 하나만 연결 (XOR)
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "question_id")
  private Question question;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "custom_question_id")
  private CustomQuestion customQuestion;

  @Column(name = "delivered_at")
  private LocalDateTime deliveredAt;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false, length = 20)
  @Builder.Default
  private QuestionInstanceStatus status = QuestionInstanceStatus.PENDING;

  @Column(name = "completed_at")
  private LocalDateTime completedAt;

  @CreatedDate
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @LastModifiedDate
  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

  // --- 도메인 검증(XOR) ---
  @PrePersist
  private void validateXor() {
    boolean hasQ = this.question != null;
    boolean hasCQ = this.customQuestion != null;
    if (hasQ == hasCQ) {
      throw new QuestionInstanceInvalidXorException();
    }
  }

  public boolean isStandard() {
    return this.question != null;
  }

  public boolean isCustom() {
    return this.customQuestion != null;
  }

  public void markCompleted(LocalDateTime now) {
    this.status = QuestionInstanceStatus.COMPLETED;
    this.completedAt = now;
  }
}
