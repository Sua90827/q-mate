package com.qmate.domain.questionrating.service;

import com.qmate.domain.questionrating.model.response.CategoryLikeStat;
import com.qmate.domain.questionrating.model.response.CategoryLikeStatsResponse;
import com.qmate.domain.questionrating.repository.QuestionRatingQueryRepository;
import com.qmate.domain.questionrating.repository.QuestionRatingRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class QuestionRatingStatsService {

  private static final ZoneId KST = ZoneId.of("Asia/Seoul");
  private static final DateTimeFormatter YM = DateTimeFormatter.ofPattern("yyyy-MM");

  private final QuestionRatingRepository questionRatingRepository;

  /**
   * 전월(1일 00:00 ~ 말일 23:59:59) 동안의 카테고리별 좋아요 수 집계
   * @param userId   요청자 ID (user.currentMatchId == matchId 검증 포함)
   * @param matchId  매치 ID
   * @param anchorDate 기준일(테스트/백필용). null이면 오늘 기준 전월.
   */
  @Transactional(readOnly = true)
  public CategoryLikeStatsResponse getPrevMonthLikesByCategory(Long userId, Long matchId, LocalDate anchorDate) {
    // 1) 전월 범위 계산 (KST 기준)
    LocalDate base = (anchorDate != null) ? anchorDate : LocalDate.now(KST);
    YearMonth prevYm = YearMonth.from(base).minusMonths(1);

    LocalDateTime from = prevYm.atDay(1).atStartOfDay();               // 00:00:00
    LocalDateTime to   = prevYm.atEndOfMonth().atTime(23, 59, 59);     // 23:59:59

    // 2) 레포지토리 조회
    List<CategoryLikeStat> stats = questionRatingRepository.findMonthlyLikesByCategory(userId, matchId, from, to);

    // 3) 합계 계산
    long total = stats.stream()
        .map(CategoryLikeStat::getLikeCount)
        .mapToLong(v -> v)
        .sum();

    // 4) 응답 조립
    return CategoryLikeStatsResponse.builder()
        .matchId(matchId)
        .month(prevYm)
        .from(from)
        .to(to)
        .totalLikes(total)
        .categories(stats)
        .build();
  }

  @Transactional(readOnly = true)
  public CategoryLikeStatsResponse getPrevMonthLikesByCategory(Long userId, Long matchId) {
    return getPrevMonthLikesByCategory(userId, matchId, null);
  }
}
