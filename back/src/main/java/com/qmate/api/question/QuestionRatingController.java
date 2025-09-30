package com.qmate.api.question;

import com.qmate.common.constants.question.QuestionRatingConstants;
import com.qmate.domain.questionrating.model.request.QuestionRatingRequest;
import com.qmate.domain.questionrating.model.response.CategoryLikeStatsResponse;
import com.qmate.domain.questionrating.model.response.QuestionRatingResponse;
import com.qmate.domain.questionrating.service.AdminRatingRebuildService;
import com.qmate.domain.questionrating.service.QuestionRatingService;
import com.qmate.domain.questionrating.service.QuestionRatingStatsService;
import com.qmate.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
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
@Tag(name = "QuestionRating", description = "질문 좋아요/평가 API")
@SecurityRequirement(name = "bearerAuth")
public class QuestionRatingController {

  private final QuestionRatingService questionRatingService;
  private final QuestionRatingStatsService questionRatingStatsService;
  private final AdminRatingRebuildService questionRatingRebuildService;

  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = "질문 평가 생성", description = QuestionRatingConstants.CREATE_MD,
      parameters = {
          @Parameter(name = "questionId", description = "평가할 질문의 ID", required = true)
      }
  )
  @PostMapping("/questions/{questionId}/ratings")
  public ResponseEntity<QuestionRatingResponse> create(
      @PathVariable Long questionId,
      @Valid @RequestBody QuestionRatingRequest request
  ) {
    QuestionRatingResponse res = questionRatingService.create(questionId, request);
    return ResponseEntity.status(HttpStatus.CREATED).body(res);
  }

  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(summary = "질문 평가 집계 재생성 (관리자 전용)", description = "모든 질문에 대한 평가 집계를 재생성합니다.")
  @PostMapping("/admin/questions/ratings/rebuild")
  public ResponseEntity<Void> rebuildAll() {
    questionRatingRebuildService.rebuildAll();
    return ResponseEntity.noContent().build();
  }

  @Operation(
      summary = "전월 카테고리별 좋아요 통계 조회",
      description = """
          전월(1일 00:00 ~ 말일 23:59:59) 동안의 매치네 카테고리별 좋아요 수 집계를 조회합니다.\n\n
          anchorDate 파라미터로 기준일을 지정할 수 있으며, 지정하지 않으면 현재 날짜를 기준으로 전월이 계산됩니다.""",
      parameters = {
          @Parameter(name = "matchId", description = "매치 ID", required = true),
          @Parameter(name = "anchorDate", description = "기준일 (ISO 8601 형식, 예: 2024-06-15). 지정하지 않으면 오늘 날짜 기준.")
      }
  )
  @GetMapping("/matches/{matchId}/stats/likes-by-category/monthly")
  public ResponseEntity<CategoryLikeStatsResponse> getPrevMonthLikesByCategory(
      @AuthenticationPrincipal UserPrincipal principal,
      @PathVariable Long matchId,
      @RequestParam(required = false)
      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate anchorDate
  ) {
    Long userId = principal.userId();
    return ResponseEntity.ok(questionRatingStatsService.getPrevMonthLikesByCategory(userId, matchId, anchorDate));
  }
}
