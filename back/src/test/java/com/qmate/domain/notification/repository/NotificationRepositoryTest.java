package com.qmate.domain.notification.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.qmate.config.JpaConfig;
import com.qmate.config.QuerydslConfig;
import com.qmate.domain.notification.entity.Notification;
import com.qmate.domain.notification.entity.NotificationCategory;
import com.qmate.domain.notification.entity.NotificationCode;
import com.qmate.domain.notification.entity.NotificationResourceType;
import com.qmate.domain.notification.repository.NotificationRepository;
import com.qmate.domain.user.User;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@DataJpaTest
@Import({JpaConfig.class, QuerydslConfig.class})
@Tag("local")
class NotificationRepositoryTest {

  @Autowired
  EntityManager em;
  @Autowired
  NotificationRepository notificationRepository;

  private User saveUser(Long currentMatchId) {
    User u = new User();
    u.setEmail("test@test.com");
    u.setNickname("nick");
    u.setCurrentMatchId(currentMatchId);
    em.persist(u);
    return u;
  }

  private Notification saveNotif(Long userId, Long matchId, boolean read,
      NotificationCategory cat, NotificationCode code) {
    Notification n = Notification.builder()
        .userId(userId)
        .matchId(matchId)
        .category(cat)
        .code(code)
        .listTitle("list")
        .pushTitle("push")
        .resourceType(NotificationResourceType.NONE)
        .resourceId(null)
        .readAt(read ? LocalDateTime.now() : null)
        .build();
    em.persist(n);
    return n;
  }

  @Test
  @DisplayName("현재 매치이거나 매치 없음 → 상세 조회 가능")
  void findAuthorizedDetail_matchesCurrentOrNull_returnsPresent() {
    // given
    User u = saveUser(10L);
    Notification n1 = saveNotif(u.getId(), 10L, false, NotificationCategory.QUESTION, NotificationCode.QI_REMINDER);
    Notification n2 = saveNotif(u.getId(), null, false, NotificationCategory.EVENT, NotificationCode.EVENT_SAME_DAY);
    em.flush(); em.clear();

    // when
    var ok1 = notificationRepository.findAuthorizedDetail(u.getId(), n1.getId());
    var ok2 = notificationRepository.findAuthorizedDetail(u.getId(), n2.getId());

    // then
    assertThat(ok1).isPresent();
    assertThat(ok2).isPresent();
  }

  @Test
  @DisplayName("unread/category/code 필터가 올바르게 적용된다")
  void findAuthorizedList_appliesUnreadCategoryAndCodeFilters() {
    // given
    User u = saveUser(10L);
    saveNotif(u.getId(), 10L, false, NotificationCategory.QUESTION, NotificationCode.QI_TODAY_READY);
    saveNotif(u.getId(), 10L, true,  NotificationCategory.QUESTION, NotificationCode.QI_TODAY_READY);
    saveNotif(u.getId(), 99L, false, NotificationCategory.QUESTION, NotificationCode.QI_TODAY_READY); // 다른 매치 → 제외
    em.flush(); em.clear();

    // when
    var page = notificationRepository.findAuthorizedList(
        u.getId(), NotificationCategory.QUESTION, NotificationCode.QI_TODAY_READY, true,
        org.springframework.data.domain.PageRequest.of(0, 20));

    // then
    assertThat(page.getTotalElements()).isEqualTo(1);
  }

  @Test
  @DisplayName("미읽음 개수는 현재 매치 기준으로만 집계된다")
  void countAuthorizedUnread_countsOnlyForCurrentMatch() {
    // given
    User u = saveUser(10L);
    saveNotif(u.getId(), 10L, false, NotificationCategory.EVENT, NotificationCode.EVENT_SAME_DAY);
    saveNotif(u.getId(), 10L, true,  NotificationCategory.EVENT, NotificationCode.EVENT_SAME_DAY);
    saveNotif(u.getId(), 99L, false, NotificationCategory.EVENT, NotificationCode.EVENT_SAME_DAY); // 제외
    em.flush(); em.clear();

    // when
    long cnt = notificationRepository.countAuthorizedUnread(u.getId());

    // then
    assertThat(cnt).isEqualTo(1);
  }
}
