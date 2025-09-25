package com.qmate.api.questioninstance;

import com.qmate.domain.questioninstance.model.request.AnswerContentRequest;
import com.qmate.domain.questioninstance.model.response.AnswerResponse;
import com.qmate.domain.questioninstance.service.AnswerService;
import com.qmate.security.UserPrincipal;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AnswerController {

  private final AnswerService answerService;

  @PostMapping("/question-instances/{questionInstanceId}/answers")
  public ResponseEntity<AnswerResponse> create(
      @PathVariable Long questionInstanceId,
      @AuthenticationPrincipal UserPrincipal principal,
      @Valid @RequestBody AnswerContentRequest request
  ) {
    Long userId = principal.userId();

    AnswerResponse response = answerService.create(questionInstanceId, userId, request);

    // Location: 생성으로 인해 최신 상태가 된 QI 상세 리소스
    URI location = URI.create("/api/question-instances/" + questionInstanceId);
    return ResponseEntity.created(location).body(response);
  }

  @PatchMapping("/answers/{answerId}")
  public ResponseEntity<AnswerResponse> update(
      @PathVariable Long answerId,
      @AuthenticationPrincipal UserPrincipal principal,
      @Valid @RequestBody AnswerContentRequest request
  ) {
    Long userId = principal.userId();

    AnswerResponse response = answerService.update(answerId, userId, request);
    return ResponseEntity.ok(response);
  }
}
