package com.qmate.api.question;

import com.qmate.domain.question.model.request.CustomQuestionTextRequest;
import com.qmate.domain.question.model.response.CustomQuestionResponse;
import com.qmate.domain.question.service.CustomQuestionService;
import com.qmate.security.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CustomQuestionController {

  private final CustomQuestionService customQuestionService;

  @PostMapping(path = "/matches/{matchId}/custom-questions")
  public ResponseEntity<CustomQuestionResponse> create(
      @AuthenticationPrincipal UserPrincipal principal,
      @PathVariable Long matchId,
      @RequestBody @Valid CustomQuestionTextRequest request) {
    Long userId = principal.userId();
    return ResponseEntity.status(HttpStatus.CREATED).body(customQuestionService.create(userId, matchId, request));
  }

  @PatchMapping(path = "/custom-questions/{id}")
  public ResponseEntity<CustomQuestionResponse> update(
      @AuthenticationPrincipal UserPrincipal principal,
      @PathVariable Long id,
      @RequestBody @Valid CustomQuestionTextRequest request) {
    Long userId = principal.userId();
    return ResponseEntity.ok(customQuestionService.update(userId, id, request));
  }

  @DeleteMapping(path = "/custom-questions/{id}")
  public ResponseEntity<Void> delete(
      @AuthenticationPrincipal UserPrincipal principal,
      @PathVariable Long id) {
    Long userId = principal.userId();
    customQuestionService.delete(userId, id);
    return ResponseEntity.noContent().build();
  }

  @GetMapping(path = "/custom-questions/{id}")
  public ResponseEntity<CustomQuestionResponse> getOne(
      @AuthenticationPrincipal UserPrincipal principal,
      @PathVariable Long id) {
    Long userId = principal.userId();
    return ResponseEntity.ok(customQuestionService.getOne(userId, id));
  }
}
