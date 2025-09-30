package com.qmate.domain.quetioninstance.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.Mockito.times;

import com.qmate.domain.questioninstance.model.response.QIDetailResponse;
import com.qmate.domain.questioninstance.repository.QuestionInstanceRepository;
import com.qmate.domain.questioninstance.service.QuestionInstanceService;
import com.qmate.exception.custom.questioninstance.QuestionInstanceNotFoundException;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class QIServiceTodayTest {

  @Mock
  QuestionInstanceRepository questionInstanceRepository;

  @Spy
  @InjectMocks
  QuestionInstanceService questionInstanceService;

  @Test
  @DisplayName("getLatestDelivered: 최신 Delivered 인스턴스를 상세로 반환(BDD+Spy)")
  void getLatestNotified_ok() {
    // given
    Long matchId = 1L, requesterId = 99L, qiId = 123L;
    given(questionInstanceRepository.findLatestDeliveredIdByMatch(matchId))
        .willReturn(Optional.of(qiId));

    QIDetailResponse expected = QIDetailResponse.builder()
        .questionInstanceId(qiId)
        .matchId(matchId)
        .build();

    // 내부 호출 getDetail(qiId, requesterId)만 스텁
    willReturn(expected).given(questionInstanceService).getDetail(qiId, requesterId);

    // when
    QIDetailResponse result = questionInstanceService.getLatestDelivered(matchId, requesterId);

    // then
    assertThat(result.getQuestionInstanceId()).isEqualTo(qiId);
    assertThat(result.getMatchId()).isEqualTo(matchId);
    then(questionInstanceRepository).should(times(1)).findLatestDeliveredIdByMatch(matchId);
    then(questionInstanceService).should(times(1)).getDetail(qiId, requesterId);
  }

  @Test
  @DisplayName("getLatestDelivered: 발송 이력 없음 → QuestionInstanceNotFoundException")
  void getLatestNotified_notFound() {
    // given
    Long matchId = 1L, requesterId = 99L;
    given(questionInstanceRepository.findLatestDeliveredIdByMatch(matchId))
        .willReturn(Optional.empty());

    // expect
    assertThatThrownBy(() ->
        questionInstanceService.getLatestDelivered(matchId, requesterId)
    ).isInstanceOf(QuestionInstanceNotFoundException.class);

    then(questionInstanceRepository).should(times(1)).findLatestDeliveredIdByMatch(matchId);
  }
}
