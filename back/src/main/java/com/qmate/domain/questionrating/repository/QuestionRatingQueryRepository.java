package com.qmate.domain.questionrating.repository;

import com.qmate.domain.questionrating.model.response.CategoryLikeStat;
import java.time.LocalDateTime;
import java.util.List;

public interface QuestionRatingQueryRepository {

  /**
   * 전월(또는 지정 기간)에 우리 매치에 노출된 관리자 질문에 대해, 우리 매치 구성원이 누른 LIKE 수를 카테고리별로 집계한다. 또한 userId가 matchId의 '현재 구성원'인지 검증을 포함한다.
   *
   * @param userId  요청자 유저 ID (현재 매치 구성원 검증용)
   * @param matchId 매치 ID
   * @param from    시작 시각 (포함)
   * @param to      종료 시각 (포함)
   * @return 카테고리별 집계 (0건 카테고리는 제외; 검증 실패 시 빈 리스트 가능)
   */
  List<CategoryLikeStat> findMonthlyLikesByCategory(Long userId, Long matchId, LocalDateTime from, LocalDateTime to);
}
