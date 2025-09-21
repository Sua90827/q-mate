package com.qmate.api.questioninstance;

import com.qmate.domain.questioninstance.model.response.QIDetailResponse;
import com.qmate.domain.questioninstance.service.QuestionInstanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// TODO 유저 권한: 일반 사용자 (인증된 사용자)
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/question-instances")
public class QuestionInstanceController {

  private final QuestionInstanceService questionInstanceService;

  @GetMapping("/{questionInstanceId}")
  public ResponseEntity<QIDetailResponse> getDetail(
      @PathVariable Long questionInstanceId
      // TODO: @AuthenticationPrincipal CustomUserDetails principal
  ) {
    Long requesterId = 1L; // TODO: principal.getUserId();
    return ResponseEntity.ok(
        questionInstanceService.getDetail(questionInstanceId, requesterId)
    );
  }
}
