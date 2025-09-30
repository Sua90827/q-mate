package com.qmate.api.question;

import com.qmate.common.constants.question.CustomQuestionConstants;
import com.qmate.domain.question.model.request.CustomQuestionStatusFilter;
import com.qmate.domain.question.model.request.CustomQuestionTextRequest;
import com.qmate.domain.question.model.response.CustomQuestionResponse;
import com.qmate.domain.question.service.CustomQuestionService;
import com.qmate.exception.ErrorResponse;
import com.qmate.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
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
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping(path = "/matches/{matchId}/custom-questions")
  @Operation(
      summary = "커스텀 질문 생성",
      description = CustomQuestionConstants.CREATE_MD,
      parameters = {
          @Parameter(name = "matchId", description = "커스텀 질문을 추가할 매치 ID", required = true)
      }
  )
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
      description = CustomQuestionConstants.UPDATE_MD,
      parameters = {
          @Parameter(name = "id", description = "수정할 커스텀 질문 ID", required = true)
      }
  )
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
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @DeleteMapping(path = "/custom-questions/{id}")
  @Operation(
      summary = "커스텀 질문 삭제",
      description = CustomQuestionConstants.DELETE_MD,
      parameters = {
          @Parameter(name = "id", description = "삭제할 커스텀 질문 ID", required = true)
      }
  )
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
      description = CustomQuestionConstants.GET_ONE_MD,
      parameters = {
          @Parameter(name = "id", description = "조회할 커스텀 질문 ID", required = true)
      }
  )
  public ResponseEntity<CustomQuestionResponse> getOne(
      @AuthenticationPrincipal UserPrincipal principal,
      @PathVariable Long id) {
    Long userId = principal.userId();
    return ResponseEntity.ok(customQuestionService.getOne(userId, id));
  }

  @GetMapping("/matches/{matchId}/custom-questions")
  @Operation(
      summary = "내 커스텀 질문 리스트 조회",
      description = CustomQuestionConstants.LIST_MD,
      parameters = {
          @Parameter(name = "matchId", in = ParameterIn.PATH, description = "매치 id"),
          @Parameter(name = "status", description = "상태 필터", schema = @Schema(implementation = CustomQuestionStatusFilter.class)),
          @Parameter(name = "sort", description = CustomQuestionConstants.SORT_DESCRIPTION)
      }
  )
  public Page<CustomQuestionResponse> list(
      @AuthenticationPrincipal UserPrincipal principal,
      @PathVariable Long matchId,
      @RequestParam(required = false) CustomQuestionStatusFilter status,
      @PageableDefault(page = 0, size = 20, sort = CustomQuestionConstants.SORT_KEY_CREATED_AT, direction = Sort.Direction.DESC)
      @ParameterObject Pageable pageable
  ) {
    return customQuestionService.findPageByOwnerAndStatusFilter(principal.userId(), matchId, status, pageable);
  }
}
