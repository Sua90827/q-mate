package com.qmate.domain.notification.repository;

import com.qmate.domain.notification.entity.Notification;
import com.qmate.domain.notification.entity.NotificationCategory;
import com.qmate.domain.notification.entity.NotificationCode;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

  @Query("""
      select n
      from Notification n, User u
      where u.id = :userId
        and n.id = :notificationId
        and n.userId = u.id
        and (n.matchId is null or u.currentMatchId = n.matchId)
      """)
  Optional<Notification> findAuthorizedDetail(@Param("userId") Long userId,
      @Param("notificationId") Long notificationId);

  @Query("""
      select n
        from Notification n, User u
       where u.id = :userId
         and n.userId = u.id
         and (n.matchId is null or u.currentMatchId = n.matchId)
         and (:category is null or n.category = :category)
         and (:code     is null or n.code     = :code)
         and (
              :unread is null
              or (:unread = true  and n.readAt is null)
              or (:unread = false and n.readAt is not null)
         )
       order by n.createdAt desc, n.id desc
      """)
  Page<Notification> findAuthorizedList(
      @Param("userId") Long userId,
      @Param("category") NotificationCategory category, // nullable
      @Param("code") NotificationCode code, // nullable
      @Param("unread") Boolean unread, // nullable
      Pageable pageable);

  @Query("""
      select count(n)
        from Notification n, User u
       where u.id = :userId
         and n.userId = u.id
         and (n.matchId is null or u.currentMatchId = n.matchId)
         and n.readAt is null
      """)
  long countAuthorizedUnread(@Param("userId") Long userId);
}