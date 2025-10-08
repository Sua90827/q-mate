package com.qmate.domain.notification.service;

import com.qmate.domain.notification.entity.Notification;
import com.qmate.domain.notification.entity.NotificationCategory;
import com.qmate.domain.notification.entity.NotificationCode;
import com.qmate.domain.notification.model.response.NotificationListItem;
import com.qmate.domain.notification.model.response.NotificationResponse;
import com.qmate.domain.notification.repository.NotificationRepository;
import com.qmate.exception.custom.notification.NotificationNotFoundException;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationService {

  private final NotificationRepository notificationRepository;

  /**
   * 알림 상세 조회
   *
   * @param userId         조회 대상 사용자 ID
   * @param notificationId 알림 ID
   * @return 알림 상세
   * @throws NotificationNotFoundException 권한이 없거나 존재하지 않는 알림인 경우
   */
  @Transactional
  public NotificationResponse getDetail(Long userId, Long notificationId) {
    Notification n = notificationRepository.findAuthorizedDetail(userId, notificationId)
        .orElseThrow(NotificationNotFoundException::new);

    if (n.getReadAt() == null) {
      n.setReadAt(LocalDateTime.now());
    }

    return NotificationResponse.from(n);
  }

  /**
   * 알림 리스트 조회
   *
   * @param userId   조회 대상 사용자 ID
   * @param category 알림 카테고리 (nullable)
   * @param code     알림 코드 (nullable)
   * @param unread   읽지 않은 알림만 조회 여부 (nullable)
   * @param pageable 페이지 정보
   * @return 알림 리스트 (페이지네이션)
   */
  @Transactional(readOnly = true)
  public Page<NotificationListItem> getList(
      Long userId, NotificationCategory category, NotificationCode code, Boolean unread, Pageable pageable) {

    // sort는 무조건 createdAt desc, id desc
    PageRequest pr = PageRequest.of(
        pageable.getPageNumber(),
        pageable.getPageSize(),
        Sort.by(Sort.Direction.DESC, "createdAt").and(Sort.by(Sort.Direction.DESC, "id"))
    );

    return notificationRepository.findAuthorizedList(userId, category, code, unread, pr)
        .map(NotificationListItem::from);
  }

  /**
   * 읽지 않은 알림 개수 조회
   */
  @Transactional(readOnly = true)
  public long getUnreadCount(Long userId) {
    return notificationRepository.countAuthorizedUnread(userId);
  }

  /**
   * 본인 소유 알림인지 확인 후 삭제 (204 No Content 용)
   */
  public void deleteAuthorized(long userId, long notificationId) {
    Notification n = notificationRepository.findAuthorizedDetail(userId, notificationId)
        .orElseThrow(NotificationNotFoundException::new);
    notificationRepository.delete(n);
  }
}
