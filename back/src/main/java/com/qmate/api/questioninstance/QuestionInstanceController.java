package com.qmate.api.questioninstance;

import com.qmate.domain.questioninstance.entity.InstanceStatus;
import com.qmate.domain.questioninstance.model.response.QIDetailResponse;
import com.qmate.domain.questioninstance.model.response.QIListItem;
import com.qmate.domain.questioninstance.service.QuestionInstanceService;
import com.qmate.exception.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// TODO 유저 권한: 일반 사용자 (인증된 사용자)
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "QuestionInstances", description = "질문 인스턴스 조회 API")
public class QuestionInstanceController {

  private final QuestionInstanceService questionInstanceService;

  @Operation(
      summary = "질문 인스턴스 상세 조회",
      description = """
          단일 질문 인스턴스의 상세 정보를 반환합니다.
          - 권한: 요청자의 currentMatchId와 해당 인스턴스의 matchId가 일치해야 함
          """,
      responses = {
          @ApiResponse(responseCode = "200", description = "성공",
              content = @Content(schema = @Schema(implementation = QIDetailResponse.class))),
          @ApiResponse(responseCode = "401", description = "인증 실패",
              content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
          @ApiResponse(responseCode = "403", description = "권한 없음",
              content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
          @ApiResponse(responseCode = "404", description = "리소스 없음",
              content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
      },
      parameters = {
          @Parameter(name = "questionInstanceId", description = "질문 인스턴스 ID", required = true)
      }
  )
  @GetMapping("/question-instances/{questionInstanceId}")
  // @GetMapping("/question-instances/{questionInstanceId}")
  public ResponseEntity<QIDetailResponse> getDetail(
      @PathVariable Long questionInstanceId
      // TODO: @AuthenticationPrincipal CustomUserDetails principal
  ) {
    Long requesterId = 1L; // TODO: principal.getUserId();
    return ResponseEntity.ok(
        questionInstanceService.getDetail(questionInstanceId, requesterId)
    );
  }

  @Operation(
      summary = "질문 인스턴스 목록 조회",
      description = """
          특정 매치에 속한 질문 인스턴스들의 페이징된 목록을 반환합니다.
          - 필터링: 상태(status), 기간(from, to)
          - 정렬: 기본 deliveredAt 내림차순
          - 권한: 요청자의 currentMatchId와 matchId가 일치해야 함
          """,
      responses = {
          @ApiResponse(responseCode = "200", description = "성공",
              content = @Content(schema = @Schema(implementation = QIListItem.class))),
          @ApiResponse(responseCode = "401", description = "인증 실패",
              content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
          @ApiResponse(responseCode = "403", description = "권한 없음",
              content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      },
      parameters = {
          @Parameter(name = "matchId", description = "매치 ID", required = true),
          @Parameter(name = "status", description = "질문 인스턴스 상태 (optional)",
              schema = @Schema(implementation = InstanceStatus.class)),
          @Parameter(name = "from", description = "deliveredAt 시작 범위 (inclusive, optional)",
              example = "2025-09-01T00:00:00"),
          @Parameter(name = "to", description = "deliveredAt 종료 범위 (exclusive, optional)",
              example = "2025-10-01T00:00:00"),
      }
  )
  @GetMapping("/matches/{matchId}/question-instances")
  // @GetMapping("/matches/{matchId}/question-instances")
  public ResponseEntity<Page<QIListItem>> list(
      @PathVariable Long matchId,
      @RequestParam(required = false) InstanceStatus status,
      @RequestParam(required = false)
      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
      @RequestParam(required = false)
      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
      @PageableDefault(size = 20, sort = "deliveredAt", direction = Sort.Direction.DESC)
      @ParameterObject Pageable pageable
      // TODO: @AuthenticationPrincipal CustomUserDetails principal
  ) {
    Long userId = 1L; // TODO: principal.getUserId();
    Page<QIListItem> page = questionInstanceService.list(userId, matchId, status, from, to, pageable);
    return ResponseEntity.ok(page);
  }
}
