package com.qmate.common.push;

import com.qmate.domain.notification.entity.Notification;

/**
 * Notification만 받아 내부에서 모든 전송 절차(설정 확인, 구독 조회/폐기, 전송)를 수행한다.
 */
public interface PushSender {
  void send(Notification notification);
}
