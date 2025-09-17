package com.qmate.api;

import com.qmate.domain.user.UserService;
import com.qmate.domain.user.model.request.RegisterRequest;
import com.qmate.domain.user.model.response.RegisterResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
  private final UserService userService;

  @PostMapping("/register")
  public ResponseEntity<RegisterResponse> registerResponseResponseEntity(@RequestBody @Valid RegisterRequest req){
    Long id = userService.register(req);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(new RegisterResponse(id.toString(), true));
  }

}
