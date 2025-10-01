package com.qmate.domain.questionrating.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.*;

import com.qmate.domain.questionrating.model.response.CategoryLikeStat;
import com.qmate.domain.questionrating.model.response.CategoryLikeStatsResponse;
import com.qmate.domain.questionrating.repository.QuestionRatingQueryRepository;
import com.qmate.domain.questionrating.repository.QuestionRatingRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class QuestionRatingStatsServiceTest {

  @Mock
  QuestionRatingRepository queryRepository;

  @InjectMocks
  QuestionRatingStatsService service;

  @Captor
  ArgumentCaptor<LocalDateTime> fromCaptor;
  @Captor
  ArgumentCaptor<LocalDateTime> toCaptor;

  @Test
  void getPrevMonthLikesByCategory_전월범위계산_합계검증() {
    // given
    Long userId = 10L;
    Long matchId = 123L;
    LocalDate anchor = LocalDate.of(2025, 9, 30); // → 전월: 2025-08-01 ~ 2025-08-31 23:59:59

    List<CategoryLikeStat> stub = List.of(
        CategoryLikeStat.builder().categoryId(1L).categoryName("일상").likeCount(10L).build(),
        CategoryLikeStat.builder().categoryId(3L).categoryName("가치관").likeCount(12L).build()
    );
    given(queryRepository.findMonthlyLikesByCategory(eq(userId), eq(matchId), any(LocalDateTime.class), any(LocalDateTime.class)))
        .willReturn(stub);

    // when
    CategoryLikeStatsResponse resp = service.getPrevMonthLikesByCategory(userId, matchId, anchor);

    // then: 레포 호출 파라미터(from/to) 범위 검증
    then(queryRepository).should().findMonthlyLikesByCategory(eq(userId), eq(matchId), fromCaptor.capture(), toCaptor.capture());
    LocalDateTime from = fromCaptor.getValue();
    LocalDateTime to = toCaptor.getValue();

    assertThat(from).isEqualTo(LocalDateTime.of(2025, 8, 1, 0, 0, 0));
    assertThat(to).isEqualTo(LocalDateTime.of(2025, 8, 31, 23, 59, 59));

    // 응답 검증
    assertThat(resp.getMatchId()).isEqualTo(matchId);
    assertThat(resp.getMonth()).isEqualTo(YearMonth.of(2025, 8));
    assertThat(resp.getFrom()).isEqualTo(from);
    assertThat(resp.getTo()).isEqualTo(to);
    assertThat(resp.getTotalLikes()).isEqualTo(22L);
    assertThat(resp.getCategories()).hasSize(2);
  }
}
