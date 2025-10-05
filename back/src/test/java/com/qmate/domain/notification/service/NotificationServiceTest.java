package com.qmate.domain.notification.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.qmate.domain.notification.entity.Notification;
import com.qmate.domain.notification.entity.NotificationCategory;
import com.qmate.domain.notification.entity.NotificationCode;
import com.qmate.domain.notification.entity.NotificationResourceType;
import com.qmate.domain.notification.model.response.NotificationResponse;
import com.qmate.domain.notification.repository.NotificationRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

  @Mock
  NotificationRepository repo;
  @InjectMocks
  NotificationService service;

  @Test
  @DisplayName("상세 조회 시 미읽음이면 읽음 처리된다")
  void getDetail_marksAsRead() {
    // given
    Notification n = Notification.builder()
        .userId(1L)
        .matchId(10L)
        .category(NotificationCategory.QUESTION)
        .code(NotificationCode.QI_TODAY_READY)
        .listTitle("list")
        .pushTitle("push")
        .resourceType(NotificationResourceType.NONE)
        .build();
    given(repo.findAuthorizedDetail(1L, 100L)).willReturn(Optional.of(n));

    // when
    NotificationResponse res = service.getDetail(1L, 100L);

    // then
    assertThat(res.getReadAt()).isNotNull(); // 읽음 처리 반영
  }
}