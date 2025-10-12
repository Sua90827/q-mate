package com.qmate.domain.user;

import com.qmate.domain.event.service.EventAnniversaryService;
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
  private final EventAnniversaryService eventAnniversaryService;

  @Transactional
  public boolean updateProfile(Long userId, String nickname, LocalDate birthDate) {
    User u = userRepository.findById(userId).orElseThrow();
    boolean changed = false;
    LocalDate oldBirth = u.getBirthDate();

    if (nickname != null) {
      if (nickname.length() > 50) {
        throw new NicknameTooLongException();
      }
      if (!nickname.equals(u.getNickname())) {
        u.setNickname(nickname);
        changed = true;
      }
    }

    if (birthDate != null) {
      if (birthDate.isAfter(LocalDate.now())) {
        throw new BirthDateInFutureException();
      }
      if (!birthDate.equals(u.getBirthDate())) {
        u.setBirthDate(birthDate);
        changed = true;
        //생일 이벤트 갱신(eventAt을 new birthDate로 바꿈)
        eventAnniversaryService.updateBirthdayEvents(userId, oldBirth, birthDate);

      }
    }
    return changed;
  }

  public String findValue(Long userId) {
    User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
    return user.getNickname();
  }
}
