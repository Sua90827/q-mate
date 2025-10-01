package com.qmate.domain.questionrating.model.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryLikeStatsResponse {

  private Long matchId;
  @JsonFormat(pattern = "yyyy-MM")
  @Schema(type = "string", pattern = "\\d{4}-\\d{2}", example = "2025-09", description = "집계 대상 월 (yyyy-MM)")
  private YearMonth month;
  private LocalDateTime from;
  private LocalDateTime to;
  private long totalLikes;
  private List<CategoryLikeStat> categories;
}
