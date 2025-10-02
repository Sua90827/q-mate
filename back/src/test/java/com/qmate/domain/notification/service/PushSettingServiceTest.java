package com.qmate.domain.notification.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.qmate.domain.notification.model.response.PushSettingResponse;
import com.qmate.domain.user.User;
import com.qmate.domain.user.UserRepository;
import com.qmate.exception.custom.matchinstance.UserNotFoundException;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PushSettingServiceTest {

  @Mock
  UserRepository userRepository;

  @InjectMocks
  PushSettingService service;

  @Test
  @DisplayName("GET: 현재 pushEnabled 값을 반환한다")
  void get_returns_current_flag() {
    User u = new User();
    u.setId(1L);
    u.setPushEnabled(true);
    given(userRepository.findById(1L)).willReturn(Optional.of(u));

    PushSettingResponse res = service.get(1L);

    assertThat(res.isPushEnabled()).isTrue();
  }

  @Test
  @DisplayName("PATCH: 값이 다르면 변경하고 변경값을 반환한다")
  void update_changes_when_different() {
    User u = new User();
    u.setId(1L);
    u.setPushEnabled(false);
    given(userRepository.findById(1L)).willReturn(Optional.of(u));

    PushSettingResponse res = service.update(1L, true);

    assertThat(u.isPushEnabled()).isTrue();
    assertThat(res.isPushEnabled()).isTrue();
  }

  @Test
  @DisplayName("PATCH: 값이 같으면 업데이트를 스킵하고 그대로 반환한다")
  void update_skips_when_same() {
    User u = new User();
    u.setId(1L);
    u.setPushEnabled(true);
    given(userRepository.findById(1L)).willReturn(Optional.of(u));

    PushSettingResponse res = service.update(1L, true);

    assertThat(u.isPushEnabled()).isTrue();
    assertThat(res.isPushEnabled()).isTrue();
  }

}
