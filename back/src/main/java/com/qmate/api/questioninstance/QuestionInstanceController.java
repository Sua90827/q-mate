package com.qmate.api.questioninstance;

import com.qmate.common.constants.questioninstance.QuestionInstanceConstants;
import com.qmate.domain.questioninstance.entity.QuestionInstanceStatus;
import com.qmate.domain.questioninstance.model.response.QIDetailResponse;
import com.qmate.domain.questioninstance.model.response.QIListItem;
import com.qmate.domain.questioninstance.service.QuestionInstanceService;
import com.qmate.exception.ErrorResponse;
import com.qmate.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "QuestionInstances", description = "질문 인스턴스 조회 API")
@SecurityRequirement(name = "bearerAuth")
public class QuestionInstanceController {

  private final QuestionInstanceService questionInstanceService;

  @Operation(
      summary = "질문 인스턴스 상세 조회",
      description = QuestionInstanceConstants.DETAIL_MD,
      parameters = {
          @Parameter(name = "questionInstanceId", description = "질문 인스턴스 ID", required = true)
      }
  )
  @GetMapping("/question-instances/{questionInstanceId}")
  public ResponseEntity<QIDetailResponse> getDetail(
      @PathVariable Long questionInstanceId,
      @AuthenticationPrincipal UserPrincipal principal
  ) {
    Long requesterId = principal.userId();
    return ResponseEntity.ok(
        questionInstanceService.getDetail(questionInstanceId, requesterId)
    );
  }

  @Operation(
      summary = "오늘 질문 조회(가장 최신의 delivered_at)",
      description = QuestionInstanceConstants.TODAY_MD,
      parameters = {
          @Parameter(name = "matchId", description = "매칭 ID", required = true)
      }
  )
  @GetMapping("/matches/{matchId}/questions/today")
  public ResponseEntity<QIDetailResponse> getLatestDeliveredForMatch(
      @PathVariable Long matchId,
      @AuthenticationPrincipal UserPrincipal principal
  ) {
    Long requesterId = principal.userId();
    QIDetailResponse body = questionInstanceService.getLatestDelivered(matchId, requesterId);
    return ResponseEntity.ok(body);
  }

  @Operation(
      summary = "질문 인스턴스 목록 조회(제공받은 질문 조회/ 답변한 질문 조회)",
      description = QuestionInstanceConstants.LIST_MD,
      parameters = {
          @Parameter(name = "matchId", description = "매치 ID", required = true),
          @Parameter(name = "status", description = "질문 인스턴스 상태 (optional)",
              schema = @Schema(implementation = QuestionInstanceStatus.class)),
          @Parameter(name = "from", description = "deliveredAt 시작 범위 (inclusive, optional)",
              example = "2025-09-01T00:00:00"),
          @Parameter(name = "to", description = "deliveredAt 종료 범위 (exclusive, optional)",
              example = "2025-10-01T00:00:00"),
          @Parameter(
              name = "sort",
              description = QuestionInstanceConstants.SORT_DESCRIPTION
          ),
      }
  )
  @GetMapping("/matches/{matchId}/question-instances")
  public ResponseEntity<Page<QIListItem>> list(
      @PathVariable Long matchId,
      @RequestParam(required = false) QuestionInstanceStatus status,
      @RequestParam(required = false)
      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
      @RequestParam(required = false)
      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
      @PageableDefault(size = 20, sort = "deliveredAt", direction = Sort.Direction.DESC)
      @ParameterObject Pageable pageable,
      @AuthenticationPrincipal UserPrincipal principal
  ) {
    Long userId = principal.userId();
    Page<QIListItem> page = questionInstanceService.list(userId, matchId, status, from, to, pageable);
    return ResponseEntity.ok(page);
  }
}
