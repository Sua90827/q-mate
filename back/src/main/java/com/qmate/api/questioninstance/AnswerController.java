package com.qmate.api.questioninstance;

import com.qmate.common.constants.questioninstance.AnswerConstants;
import com.qmate.domain.questioninstance.model.request.AnswerContentRequest;
import com.qmate.domain.questioninstance.model.response.AnswerResponse;
import com.qmate.domain.questioninstance.service.AnswerService;
import com.qmate.exception.ErrorResponse;
import com.qmate.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "Answer", description = "답변 제출/수정 API")
@SecurityRequirement(name = "bearerAuth")
public class AnswerController {

  private final AnswerService answerService;

  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/question-instances/{questionInstanceId}/answers")
  @Operation(
      summary = "답변 제출 (최초 1회)",
      description = AnswerConstants.CREATE_MD,
      parameters = {
          @Parameter(name = "questionInstanceId", in = ParameterIn.PATH, description = "답변을 제출할 질문 인스턴스 ID", required = true, example = "123")
      }
  )
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
  @Operation(
      summary = "답변 수정 (완료 이전 한정)",
      description = AnswerConstants.UPDATE_MD,
      parameters = {
          @Parameter(name = "answerId", in = ParameterIn.PATH, description = "수정할 답변 ID", required = true, example = "456"),
      }
  )
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
