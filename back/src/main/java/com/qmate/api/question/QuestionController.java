package com.qmate.api.question;

import com.qmate.common.constants.CommonConstants;
import com.qmate.common.constants.HttpStatusCode;
import com.qmate.common.constants.question.QuestionConstants;
import com.qmate.domain.question.entity.RelationType;
import com.qmate.domain.question.model.request.QuestionCreateRequest;
import com.qmate.domain.question.model.request.QuestionUpdateRequest;
import com.qmate.domain.question.model.response.QuestionResponse;
import com.qmate.domain.question.service.QuestionService;
import com.qmate.exception.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/questions")
@RequiredArgsConstructor
@Tag(name = "Question (Admin)", description = "질문 관리 API")
@SecurityRequirement(name = "bearerAuth")
public class QuestionController {

  private final QuestionService questionService;

  /**
   * 질문 생성
   */
  @PostMapping
  @Operation(
      summary = "질문 생성",
      description = "새로운 질문을 생성합니다.",
      responses = {
          @ApiResponse(responseCode = HttpStatusCode.CREATED, description = "생성 성공", content = @Content(mediaType = CommonConstants.MEDIA_TYPE_JSON, schema = @Schema(implementation = QuestionResponse.class))),
      },
      requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
          required = true,
          content = @Content(mediaType = CommonConstants.MEDIA_TYPE_JSON, schema = @Schema(implementation = QuestionCreateRequest.class))
      )
  )
  // @PostMapping
  public ResponseEntity<QuestionResponse> createQuestion(
      @RequestBody @Valid QuestionCreateRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(questionService.createQuestion(request));
  }

  /**
   * 질문 수정
   */
  @PatchMapping("/{id}")
  @Operation(
      summary = "질문 수정",
      description = QuestionConstants.UPDATE_MD,
      responses = {
          @ApiResponse(responseCode = HttpStatusCode.OK, description = "수정 성공", content = @Content(mediaType = CommonConstants.MEDIA_TYPE_JSON, schema = @Schema(implementation = QuestionResponse.class))),
      },
      requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
          required = true,
          content = @Content(mediaType = CommonConstants.MEDIA_TYPE_JSON, schema = @Schema(implementation = QuestionUpdateRequest.class))
      ),
      parameters = {
          @Parameter(name = "id", description = "수정할 질문 ID", required = true)
      }
  )
  //@PatchMapping("/{id}")
  public ResponseEntity<QuestionResponse> updateQuestion(
      @PathVariable Long id,
      @RequestBody @Valid QuestionUpdateRequest request) {
    return ResponseEntity.ok(questionService.updateQuestion(id, request));
  }

  /**
   * 질문 전체 조회 (필터링 및 페이징)
   */
  @GetMapping
  @Operation(
      summary = "질문 전체 조회 (필터링 및 페이징)",
      description = "질문을 필터링 조건에 따라 페이징하여 조회합니다.",
      responses = {
          @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(mediaType = CommonConstants.MEDIA_TYPE_JSON, schema = @Schema(implementation = QuestionResponse.class))),
      },
      parameters = {
          @Parameter(name = "relationType", description = "질문의 관계 타입", required = false, schema = @Schema(implementation = RelationType.class)),
          @Parameter(name = "categoryId", description = "질문의 카테고리 ID", required = false),
          @Parameter(name = "active", description = "활성화 여부 (true/false)", required = false),
      }
  )
  //@GetMapping
  public ResponseEntity<Page<QuestionResponse>> getQuestions(
      @RequestParam(required = false) RelationType relationType,
      @RequestParam(required = false) Long categoryId,
      @RequestParam(name = "active", required = false) Boolean active,
      @PageableDefault(page = 0, size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
      @ParameterObject Pageable pageable) {
    Page<QuestionResponse> result = questionService.getQuestions(relationType, categoryId, active, pageable);
    return ResponseEntity.ok(result);
  }
}
