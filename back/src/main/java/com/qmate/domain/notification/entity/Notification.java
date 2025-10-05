package com.qmate.domain.notification.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(name = "notification")
@EntityListeners(AuditingEntityListener.class)
public class Notification {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "notification_id")
  private Long id;

  @Column(name = "user_id", nullable = false)
  private Long userId;

  @Column(name = "match_id")
  private Long matchId;

  @Enumerated(EnumType.STRING)
  @Column(name = "category", length = 30, nullable = false)
  private NotificationCategory category;

  @Enumerated(EnumType.STRING)
  @Column(name = "code", length = 40, nullable = false)
  private NotificationCode code;

  @Column(name = "list_title", length = 150, nullable = false)
  private String listTitle;

  @Column(name = "push_title", length = 150, nullable = false)
  private String pushTitle;

  @Enumerated(EnumType.STRING)
  @Column(name = "resource_type", length = 30, nullable = false)
  private NotificationResourceType resourceType;

  @Column(name = "resource_id")
  private Long resourceId;

  @Column(name = "read_at")
  private LocalDateTime readAt;

  @CreatedDate
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @LastModifiedDate
  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;
}
