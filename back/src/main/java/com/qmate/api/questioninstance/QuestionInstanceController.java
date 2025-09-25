package com.qmate.api.questioninstance;

import com.qmate.domain.questioninstance.entity.InstanceStatus;
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
public class QuestionInstanceController {

  private final QuestionInstanceService questionInstanceService;

  @Operation(
      summary = "질문 인스턴스 상세 조회",
      description = """
          단일 질문 인스턴스의 상세 정보를 반환합니다.
          - 권한: 요청자의 currentMatchId와 해당 인스턴스의 matchId가 일치해야 함
          """,
      security = @SecurityRequirement(name = "bearerAuth"),
      responses = {
          @ApiResponse(responseCode = "200", description = "성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = QIDetailResponse.class))),
          @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
          @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
          @ApiResponse(responseCode = "404", description = "리소스 없음", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
      },
      parameters = {
          @Parameter(name = "questionInstanceId", description = "질문 인스턴스 ID", required = true)
      }
  )
  @GetMapping("/question-instances/{questionInstanceId}")
  // @GetMapping("/question-instances/{questionInstanceId}")
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
      summary = "오늘 질문 조회(가장 최근 notified 인스턴스)",
      description = """
          해당 매칭에서 가장 최근에 알림(notified)된 질문 인스턴스를 상세 형태로 반환합니다.
          - 의미: 엔드포인트 명은 today지만, 날짜 경계와 무관하게 `notified_at`이 가장 최신인 1건을 반환
          - 필터: `notified_at IS NOT NULL` 인 레코드만 대상
          - 권한: 요청자의 currentMatchId(또는 소속)가 path의 matchId와 일치해야 함
          """,
      security = @SecurityRequirement(name = "bearerAuth"),
      responses = {
          @ApiResponse(responseCode = "200", description = "성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = QIDetailResponse.class))),
          @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
          @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
          @ApiResponse(responseCode = "404", description = "리소스 없음(발송 이력 없음 등)", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
      },
      parameters = {
          @Parameter(name = "matchId", description = "매칭 ID", required = true)
      }
  )
  @GetMapping("/matches/{matchId}/questions/today")
  public ResponseEntity<QIDetailResponse> getLatestNotifiedForMatch(
      @PathVariable Long matchId,
      @AuthenticationPrincipal UserPrincipal principal
  ) {
    Long requesterId = principal.userId();
    QIDetailResponse body = questionInstanceService.getLatestNotified(matchId, requesterId);
    return ResponseEntity.ok(body);
  }

  @Operation(
      summary = "질문 인스턴스 목록 조회",
      description = """
          특정 매치에 속한 질문 인스턴스들의 페이징된 목록을 반환합니다.
          - 필터링: 상태(status), 기간(from, to)
          - 정렬: 기본 deliveredAt 내림차순
          - 권한: 요청자의 currentMatchId와 matchId가 일치해야 함
          """,
      security = @SecurityRequirement(name = "bearerAuth"),
      responses = {
          @ApiResponse(responseCode = "200", description = "성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = QIListItem.class))),
          @ApiResponse(responseCode = "400", description = "미지원 sort 키 사용", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
          @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
          @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
      },
      parameters = {
          @Parameter(name = "matchId", description = "매치 ID", required = true),
          @Parameter(name = "status", description = "질문 인스턴스 상태 (optional)",
              schema = @Schema(implementation = InstanceStatus.class)),
          @Parameter(name = "from", description = "deliveredAt 시작 범위 (inclusive, optional)",
              example = "2025-09-01T00:00:00"),
          @Parameter(name = "to", description = "deliveredAt 종료 범위 (exclusive, optional)",
              example = "2025-10-01T00:00:00"),
          @Parameter(
              name = "sort",
              description = """
                  정렬 키와 방향(여러 개 지정 가능).
                  - 형식: sort=`key`[,`dir`]  (dir 기본값: asc)
                  - 허용 key: deliveredAt | completedAt | status
                  - 예: sort=deliveredAt,desc&sort=status,asc
                  """,
              array = @ArraySchema(
                  schema = @Schema(implementation = String.class,
                      allowableValues = {"deliveredAt", "completedAt", "status"})
              ),
              example = "deliveredAt,desc"
          ),
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
      @ParameterObject Pageable pageable,
      @AuthenticationPrincipal UserPrincipal principal
  ) {
    Long userId = principal.userId();
    Page<QIListItem> page = questionInstanceService.list(userId, matchId, status, from, to, pageable);
    return ResponseEntity.ok(page);
  }
}
