package com.qmate.domain.user;

import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProfileService {
  private final UserRepository userRepository;

  @Transactional
  public boolean updateProfile(Long userId, String nickname, LocalDate birthDate) {
    User u = userRepository.findById(userId).orElseThrow();
    boolean changed = false;

    if (nickname != null) {
      if (nickname.length() > 50) throw new IllegalArgumentException("닉네임은 50자 이내");
      if (!nickname.equals(u.getNickname())) {
        u.setNickname(nickname);
        changed = true;
      }
    }

    if (birthDate != null) {
      if (birthDate.isAfter(LocalDate.now())) throw new IllegalArgumentException("미래 날짜 불가");
      if (!birthDate.equals(u.getBirthDate())) {
        u.setBirthDate(birthDate);
        changed = true;
      }
    }
    return changed;
  }
}
