package com.qmate.domain.user;

import com.qmate.exception.custom.matchinstance.UserNotFoundException;
import com.qmate.exception.custom.user.BirthDateInFutureException;
import com.qmate.exception.custom.user.NicknameTooLongException;
import java.time.LocalDate;
import java.util.Optional;
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
      if (nickname.length() > 50) throw new NicknameTooLongException();
      if (!nickname.equals(u.getNickname())) {
        u.setNickname(nickname);
        changed = true;
      }
    }

    if (birthDate != null) {
      if (birthDate.isAfter(LocalDate.now())) throw new BirthDateInFutureException();
      if (!birthDate.equals(u.getBirthDate())) {
        u.setBirthDate(birthDate);
        changed = true;
      }
    }
    return changed;
  }

  public String findValue(Long userId) {
    User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
    return user.getNickname();
  }
}
