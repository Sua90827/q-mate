package com.qmate.api.questioninstance;

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
@Tag(name = "Answer", description = "답변 제출/수정 API")
public class AnswerController {

  private final AnswerService answerService;

  @PostMapping("/question-instances/{questionInstanceId}/answers")
  @Operation(
      summary = "답변 제출 (최초 1회)",
      description = "특정 질문 인스턴스(QI)에 대해 사용자가 자신의 답변을 1회 제출합니다. 성공 시 Location 헤더로 QI 상세 리소스를 반환합니다.",
      security = @SecurityRequirement(name = "bearerAuth"),
      responses = {
          @ApiResponse(
              responseCode = "201",
              description = "생성 성공",
              headers = {
                  @Header(name = "Location", description = "생성 결과가 반영된 QI 상세 리소스 URL", schema = @Schema(type = "string"))
              },
              content = @Content(mediaType = "application/json", schema = @Schema(implementation = AnswerResponse.class))
          ),
          @ApiResponse(responseCode = "400", description = "유효성 오류", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
          @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
          @ApiResponse(responseCode = "403", description = "해당 QI의 구성원 아님", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
          @ApiResponse(responseCode = "404", description = "questionInstance 없음", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
          @ApiResponse(responseCode = "409", description = "중복 제출", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
          @ApiResponse(responseCode = "432", description = "QI가 COMPLETED/EXPIRED 상태", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
          @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
      },
      parameters = {
          @Parameter(name = "questionInstanceId", in = ParameterIn.PATH, description = "답변을 제출할 질문 인스턴스 ID", required = true, example = "123")
      },
      requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
          description = "제출할 답변 내용",
          required = true,
          content = @Content(mediaType = "application/json", schema = @Schema(implementation = AnswerContentRequest.class))
      )
  )
  //@PostMapping("/question-instances/{questionInstanceId}/answers")
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
      description = "본인이 제출한 답변을 수정합니다. 완료된 답변은 수정할 수 없습니다.",
      security = @SecurityRequirement(name = "bearerAuth"),
      responses = {
          @ApiResponse(
              responseCode = "200",
              description = "수정 성공",
              content = @Content(mediaType = "application/json", schema = @Schema(implementation = AnswerResponse.class))
          ),
          @ApiResponse(responseCode = "400", description = "유효성 오류", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
          @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
          @ApiResponse(responseCode = "403", description = "해당 QI의 구성원 아님", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
          @ApiResponse(responseCode = "404", description = "해당 답변 없음", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
          @ApiResponse(responseCode = "423", description = "QI가 COMPLETED/EXPIRED 상태", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
          @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
      },
      parameters = {
          @Parameter(name = "answerId", in = ParameterIn.PATH, description = "수정할 답변 ID", required = true, example = "456"),
      },
      requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
          description = "수정할 답변 내용",
          required = true,
          content = @Content(mediaType = "application/json", schema = @Schema(implementation = AnswerContentRequest.class))
      )
  )
  //@PatchMapping("/answers/{answerId}")
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
