package com.qmate.api.question;

import com.qmate.domain.question.model.request.CustomQuestionTextRequest;
import com.qmate.domain.question.model.response.CustomQuestionResponse;
import com.qmate.domain.question.service.CustomQuestionService;
import com.qmate.exception.ErrorResponse;
import com.qmate.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Custom Question", description = "커스텀 질문 API")
@SecurityRequirement(name = "bearerAuth")
public class CustomQuestionController {

  private final CustomQuestionService customQuestionService;

  /**
   * 커스텀 질문 생성
   */
  @PostMapping(path = "/matches/{matchId}/custom-questions")
  @Operation(
      summary = "커스텀 질문 생성",
      description = "특정 매치에 대한 커스텀 질문을 생성합니다.",
      responses = {
          @ApiResponse(responseCode = "201", description = "생성 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomQuestionResponse.class))),
          @ApiResponse(responseCode = "400", description = "유효하지 않은 요청 값", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
          @ApiResponse(responseCode = "404", description = "매치를 찾을 수 없음", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
      },
      requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
          required = true,
          content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomQuestionTextRequest.class))
      ),
      parameters = {
          @Parameter(name = "matchId", description = "커스텀 질문을 추가할 매치 ID", required = true)
      }
  )
  //@PostMapping(path = "/matches/{matchId}/custom-questions")
  public ResponseEntity<CustomQuestionResponse> create(
      @AuthenticationPrincipal UserPrincipal principal,
      @PathVariable Long matchId,
      @RequestBody @Valid CustomQuestionTextRequest request) {
    Long userId = principal.userId();
    return ResponseEntity.status(HttpStatus.CREATED).body(customQuestionService.create(userId, matchId, request));
  }

  /**
   * 커스텀 질문 수정
   */
  @PatchMapping(path = "/custom-questions/{id}")
  @Operation(
      summary = "커스텀 질문 수정",
      description = "특정 커스텀 질문을 수정합니다.",
      responses = {
          @ApiResponse(responseCode = "200", description = "수정 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomQuestionResponse.class))),
          @ApiResponse(responseCode = "400", description = "유효하지 않은 요청 값", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
          @ApiResponse(responseCode = "404", description = "커스텀 질문을 찾을 수 없음", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
          @ApiResponse(responseCode = "423", description = "수정 불가", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
      },
      requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
          required = true,
          content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomQuestionTextRequest.class))
      ),
      parameters = {
          @Parameter(name = "id", description = "수정할 커스텀 질문 ID", required = true)
      }
  )
  //@PatchMapping(path = "/custom-questions/{id}")
  public ResponseEntity<CustomQuestionResponse> update(
      @AuthenticationPrincipal UserPrincipal principal,
      @PathVariable Long id,
      @RequestBody @Valid CustomQuestionTextRequest request) {
    Long userId = principal.userId();
    return ResponseEntity.ok(customQuestionService.update(userId, id, request));
  }

  /**
   * 커스텀 질문 삭제
   */
  @DeleteMapping(path = "/custom-questions/{id}")
  @Operation(
      summary = "커스텀 질문 삭제",
      description = "특정 커스텀 질문을 삭제합니다.",
      responses = {
          @ApiResponse(responseCode = "204", description = "삭제 성공"),
          @ApiResponse(responseCode = "404", description = "커스텀 질문을 찾을 수 없음", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
          @ApiResponse(responseCode = "423", description = "삭제 불가", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
      },
      parameters = {
          @Parameter(name = "id", description = "삭제할 커스텀 질문 ID", required = true)
      }
  )
  //@DeleteMapping(path = "/custom-questions/{id}")
  public ResponseEntity<Void> delete(
      @AuthenticationPrincipal UserPrincipal principal,
      @PathVariable Long id) {
    Long userId = principal.userId();
    customQuestionService.delete(userId, id);
    return ResponseEntity.noContent().build();
  }

  /**
   * 커스텀 질문 단일 조회
   */
  @GetMapping(path = "/custom-questions/{id}")
  @Operation(
      summary = "커스텀 질문 단일 조회",
      description = "특정 커스텀 질문을 조회합니다.",
      responses = {
          @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomQuestionResponse.class))),
          @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
          @ApiResponse(responseCode = "404", description = "커스텀 질문을 찾을 수 없음", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
      },
      parameters = {
          @Parameter(name = "id", description = "조회할 커스텀 질문 ID", required = true)
      }
  )
  //@GetMapping(path = "/custom-questions/{id}")
  public ResponseEntity<CustomQuestionResponse> getOne(
      @AuthenticationPrincipal UserPrincipal principal,
      @PathVariable Long id) {
    Long userId = principal.userId();
    return ResponseEntity.ok(customQuestionService.getOne(userId, id));
  }
}
