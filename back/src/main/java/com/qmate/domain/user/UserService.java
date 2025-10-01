package com.qmate.domain.user;

import com.qmate.domain.user.model.request.RegisterRequest;
import com.qmate.exception.custom.user.EmailAlreadyInUseException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public Long register(RegisterRequest req) {
    if(userRepository.existsByEmail(req.getEmail())){
      throw new EmailAlreadyInUseException();
    }
    User user = User.builder()
        .email(req.getEmail())
        .passwordHash(passwordEncoder.encode(req.getPassword()))
        .nickname(req.getNickname())
        .birthDate(req.getBirthDate())
        .build();
    return userRepository.save(user).getId();
  }
}
