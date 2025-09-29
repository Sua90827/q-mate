package com.qmate.domain.question.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.*;

import com.qmate.domain.question.model.request.CustomQuestionStatusFilter;
import com.qmate.domain.question.model.response.CustomQuestionResponse;
import com.qmate.domain.question.model.response.SourceType;
import com.qmate.domain.question.repository.CustomQuestionRepository;
import com.qmate.exception.custom.question.CustomQuestionInvalidSortKeyException;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class CustomQuestionServiceTest {

  @Mock
  CustomQuestionRepository repository;

  @InjectMocks
  CustomQuestionService service;

  private CustomQuestionResponse sampleDto(Long cqId, Long matchId, boolean editable) {
    return new CustomQuestionResponse(
        cqId,
        SourceType.CUSTOM,
        /* relationType */ null,
        matchId,
        "text-" + cqId,
        editable,
        LocalDateTime.now().minusDays(1),
        LocalDateTime.now()
    );
  }

  @Test
  @DisplayName("정상 인자 조회시 레포 결과를 그대로 반환")
  void list_pass_through() {
    // given
    Long userId = 10L, matchId = 20L;
    var pageable = PageRequest.of(0, 20);
    Page<CustomQuestionResponse> stub =
        new PageImpl<>(List.of(sampleDto(1L, matchId, true)), pageable, 1);

    given(repository.findPageByOwnerAndStatusFilter(userId, matchId, CustomQuestionStatusFilter.EDITABLE, pageable))
        .willReturn(stub);

    // when
    Page<CustomQuestionResponse> result =
        service.findPageByOwnerAndStatusFilter(userId, matchId, CustomQuestionStatusFilter.EDITABLE, pageable);

    // then
    assertThat(result.getTotalElements()).isEqualTo(1);
    assertThat(result.getContent().getFirst().isEditable()).isTrue();

    // and: 레포 호출 인자 검증
    then(repository).should(times(1))
        .findPageByOwnerAndStatusFilter(userId, matchId, CustomQuestionStatusFilter.EDITABLE, pageable);
    then(repository).shouldHaveNoMoreInteractions();
  }

  @Test
  @DisplayName("status=null 조회시 레포에 null 그대로 전달되고 모든 상태 결과를 받는다")
  void list_with_null_status() {
    // given
    Long userId = 11L, matchId = 22L;
    var pageable = PageRequest.of(1, 10);

    var dto1 = new CustomQuestionResponse(1L, SourceType.CUSTOM, null, matchId, "q1", true,
        LocalDateTime.now().minusDays(1), LocalDateTime.now());
    var dto2 = new CustomQuestionResponse(2L, SourceType.CUSTOM, null, matchId, "q2", false,
        LocalDateTime.now().minusDays(2), LocalDateTime.now());

    Page<CustomQuestionResponse> stub =
        new PageImpl<>(List.of(dto1, dto2), pageable, 2L);

    given(repository.findPageByOwnerAndStatusFilter(userId, matchId, null, pageable))
        .willReturn(stub);

    // when
    Page<CustomQuestionResponse> result =
        service.findPageByOwnerAndStatusFilter(userId, matchId, null, pageable);

    // then
    assertThat(result.getNumberOfElements()).isEqualTo(2L);
    assertThat(result.getContent())
        .extracting(CustomQuestionResponse::getCustomQuestionId)
        .containsExactly(1L, 2L);

    // and: 레포에 status=null로 전달되었는지 캡처로 확인
    ArgumentCaptor<CustomQuestionStatusFilter> statusCap = ArgumentCaptor.forClass(CustomQuestionStatusFilter.class);
    then(repository).should().findPageByOwnerAndStatusFilter(eq(userId), eq(matchId), statusCap.capture(), eq(pageable));
    assertThat(statusCap.getValue()).isNull();
  }

  @Test
  @DisplayName("레포가 잘못된 정렬키 예외를 던지면 예외를 그대로 전파한다")
  void list_propagates_invalid_sort_exception() {
    // given
    Long userId = 33L, matchId = 44L;
    var pageable = PageRequest.of(0, 10);

    given(repository.findPageByOwnerAndStatusFilter(userId, matchId, null, pageable))
        .willThrow(new CustomQuestionInvalidSortKeyException());

    // when / then
    assertThatThrownBy(() ->
        service.findPageByOwnerAndStatusFilter(userId, matchId, null, pageable))
        .isInstanceOf(CustomQuestionInvalidSortKeyException.class);

    then(repository).should().findPageByOwnerAndStatusFilter(userId, matchId, null, pageable);
  }
}
