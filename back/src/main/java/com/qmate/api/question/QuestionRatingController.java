package com.qmate.api.question;

import com.qmate.domain.questionrating.model.request.QuestionRatingRequest;
import com.qmate.domain.questionrating.model.response.QuestionRatingResponse;
import com.qmate.domain.questionrating.service.AdminRatingRebuildService;
import com.qmate.domain.questionrating.service.QuestionRatingService;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class QuestionRatingController {

  private final QuestionRatingService questionRatingService;
  private final AdminRatingRebuildService questionRatingRebuildService;

  @Operation(summary = "질문 평가 생성", description = "특정 질문에 대한 평가를 생성합니다.",
      responses = {
          @ApiResponse(responseCode = "201", description = "평가 생성 성공"),
          @ApiResponse(responseCode = "400", description = "잘못된 요청"),
          @ApiResponse(responseCode = "401", description = "인증 실패"),
          @ApiResponse(responseCode = "404", description = "리소스 없음"),
          @ApiResponse(responseCode = "409", description = "중복된 평가"),
      },
      parameters = {
          @Parameter(name = "questionId", description = "평가할 질문의 ID", required = true)
      }
  )
  @PostMapping("/questions/{questionId}/ratings")
  // @PostMapping("/questions/{questionId}/ratings")
  public ResponseEntity<QuestionRatingResponse> create(
      @PathVariable Long questionId,
      @Valid @RequestBody QuestionRatingRequest request
  ) {
    QuestionRatingResponse res = questionRatingService.create(questionId, request);
    return ResponseEntity.status(HttpStatus.CREATED).body(res);
  }

  @Operation(summary = "질문 평가 집계 재생성 (관리자 전용)", description = "모든 질문에 대한 평가 집계를 재생성합니다.",
      responses = {
          @ApiResponse(responseCode = "204", description = "재생성 성공"),
          @ApiResponse(responseCode = "401", description = "인증 실패"),
          @ApiResponse(responseCode = "403", description = "권한 없음"),
          @ApiResponse(responseCode = "500", description = "재생성 중 오류 발생"),
      }
  )
  @PostMapping("/admin/questions/ratings/rebuild")
  public ResponseEntity<Void> rebuildAll() {
    questionRatingRebuildService.rebuildAll();
    return ResponseEntity.noContent().build();
  }
}
