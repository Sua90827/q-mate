package com.qmate.domain.notification.service;

import com.qmate.domain.notification.model.response.PushSettingResponse;
import com.qmate.domain.user.User;
import com.qmate.domain.user.UserRepository;
import com.qmate.exception.custom.matchinstance.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PushSettingService {

  private final UserRepository userRepository;

  @Transactional(readOnly = true)
  public PushSettingResponse get(Long userId) {
    boolean enabled = userRepository.findById(userId)
        .orElseThrow(UserNotFoundException::new)
        .isPushEnabled();
    return PushSettingResponse.builder().pushEnabled(enabled).build();
  }

  @Transactional
  public PushSettingResponse update(Long userId, boolean enabled) {
    User u = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
    if (u.isPushEnabled() != enabled) {
      u.setPushEnabled(enabled);
    }
    return PushSettingResponse.builder().pushEnabled(enabled).build();
  }
}