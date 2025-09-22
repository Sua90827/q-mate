package com.qmate.api.questioninstance;

import com.qmate.domain.questioninstance.model.request.AnswerContentRequest;
import com.qmate.domain.questioninstance.model.response.AnswerResponse;
import com.qmate.domain.questioninstance.service.AnswerService;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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

  @PostMapping(value = "/question-instances/{questionInstanceId}/answers")
  public ResponseEntity<AnswerResponse> create(
      @PathVariable Long questionInstanceId,
      // TODO: @AuthenticationPrincipal CustomUserDetails principal
      @Valid @RequestBody AnswerContentRequest request
  ) {
    Long userId = 1L; // TODO: principal.getUserId();

    AnswerResponse response = answerService.create(questionInstanceId, userId, request);

    // Location: 생성으로 인해 최신 상태가 된 QI 상세 리소스
    URI location = URI.create("/api/question-instances/" + questionInstanceId);
    return ResponseEntity.created(location).body(response);
  }
}
