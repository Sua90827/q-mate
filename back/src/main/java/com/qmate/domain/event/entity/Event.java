package com.qmate.domain.event.entity;

import com.qmate.domain.match.Match;
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
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "`event`")
public class Event {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "event_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "match_id", nullable = false)
  private Match match;

  @Column(name = "title", nullable = false, length = 120)
  private String title;

  @Column(name = "description", length = 1000)
  private String description;

  @Column(name = "event_at", nullable = false)
  private LocalDate eventAt;

  @Enumerated(EnumType.STRING)
  @Builder.Default
  @Column(name = "repeat_type", nullable = false, length = 16)
  private EventRepeatType repeatType = EventRepeatType.NONE;

  @Enumerated(EnumType.STRING)
  @Builder.Default
  @Column(name = "alarm_option", nullable = false, length = 20)
  private EventAlarmOption alarmOption = EventAlarmOption.WEEK_BEFORE;

  @Column(name = "is_anniversary", nullable = false)
  private boolean anniversary;

  @CreatedDate
  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  @LastModifiedDate
  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;
}
