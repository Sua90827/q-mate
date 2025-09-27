package com.qmate.api.question;

import com.qmate.domain.question.model.request.QuestionCategoryCreateRequest;
import com.qmate.domain.question.model.request.QuestionCategoryUpdateRequest;
import com.qmate.domain.question.model.response.QuestionCategoryResponse;
import com.qmate.domain.question.service.QuestionCategoryService;
import com.qmate.exception.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/question-categories")
@RequiredArgsConstructor
@Tag(name = "Question Category (Admin)", description = "질문 카테고리 관리 API")
@SecurityRequirement(name = "bearerAuth")
public class QuestionCategoryController {

  private final QuestionCategoryService questionCategoryService;

  /**
   * 카테고리 생성
   */
  @PostMapping
  @Operation(
      summary = "카테고리 생성",
      description = "새로운 질문 카테고리를 생성합니다.",
      requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
          required = true,
          content = @Content(mediaType = "application/json", schema = @Schema(implementation = QuestionCategoryCreateRequest.class))
      ),
      responses = {
          @ApiResponse(responseCode = "201", description = "생성 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = QuestionCategoryResponse.class))),
          @ApiResponse(responseCode = "400", description = "유효하지 않은 요청 값", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
          @ApiResponse(responseCode = "409", description = "이미 존재하는 카테고리명", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
      }
  )
  // @PostMapping
  public ResponseEntity<QuestionCategoryResponse> createCategory(
      @RequestBody @Valid QuestionCategoryCreateRequest request) {
    QuestionCategoryResponse response = questionCategoryService.createCategory(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  /**
   * 카테고리 수정
   */
  @PatchMapping("/{id}")
  @Operation(
      summary = "카테고리 수정",
      description = "기존 질문 카테고리를 부분 수정합니다.",
      requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
          required = true,
          content = @Content(mediaType = "application/json", schema = @Schema(implementation = QuestionCategoryUpdateRequest.class))
      ),
      responses = {
          @ApiResponse(responseCode = "200", description = "수정 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = QuestionCategoryResponse.class))),
          @ApiResponse(responseCode = "400", description = "유효하지 않은 요청 값", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
          @ApiResponse(responseCode = "404", description = "대상 카테고리 없음", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
          @ApiResponse(responseCode = "409", description = "이미 존재하는 카테고리명", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
      }
  )
  // @PatchMapping("/{id}")
  public ResponseEntity<QuestionCategoryResponse> updateCategory(
      @PathVariable Long id,
      @RequestBody @Valid QuestionCategoryUpdateRequest request) {
    QuestionCategoryResponse response = questionCategoryService.updateCategory(id, request);
    return ResponseEntity.ok(response);
  }

  /**
   * 카테고리 전체 조회
   */
  @GetMapping
  @Operation(
      summary = "카테고리 전체 조회",
      description = "모든 질문 카테고리를 조회합니다.",
      responses = {
          @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = QuestionCategoryResponse.class))),
      }
  )
  //@GetMapping
  public ResponseEntity<List<QuestionCategoryResponse>> getAllCategories() {
    List<QuestionCategoryResponse> response = questionCategoryService.getAllCategories();
    return ResponseEntity.ok(response);
  }
}
