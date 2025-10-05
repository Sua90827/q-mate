package com.qmate.common.push;

import com.qmate.domain.notification.entity.Notification;

public class NoopPushSender implements PushSender {

  @Override
  public void send(Notification notification) {
    // No operation performed
  }

}
