package com.qmate.questioninstance;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

import com.qmate.domain.questioninstance.entity.InstanceStatus;
import com.qmate.domain.questioninstance.model.response.QIListItem;
import com.qmate.domain.questioninstance.repository.QuestionInstanceQueryRepository;
import com.qmate.domain.questioninstance.repository.QuestionInstanceRepository;
import com.qmate.domain.questioninstance.service.QuestionInstanceService;
import com.qmate.domain.user.UserRepository;
import com.qmate.exception.custom.UserNotFoundException;
import com.qmate.exception.custom.questioninstance.QuestionInstanceForbiddenException;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@ExtendWith(MockitoExtension.class)
public class QIServiceListTest {

  @Mock
  QuestionInstanceRepository qiRepository;
  @Mock
  UserRepository userRepository;

  @InjectMocks
  QuestionInstanceService service;

  @Nested
  @DisplayName("성공")
  class Success {

    @Test
    @DisplayName("현재 매치 일치 시 목록 반환")
    void list_ok() {
      // given
      Long userId = 1L;
      Long matchId = 10L;
      given(userRepository.findCurrentMatchIdById(userId))
          .willReturn(Optional.of(matchId));

      Page<QIListItem> expected = Page.empty(PageRequest.of(0, 20, Sort.by("deliveredAt").descending()));
      given(qiRepository.findList(eq(matchId), isNull(), isNull(), isNull(), any(Pageable.class)))
          .willReturn(expected);

      // when
      Page<QIListItem> result = service.list(
          userId, matchId, null, null, null,
          PageRequest.of(0, 20, Sort.by("deliveredAt").descending()));

      // then
      assertNotNull(result);
      assertEquals(0, result.getTotalElements());
    }
  }

  @Nested
  @DisplayName("실패")
  class Failure {

    @Test
    @DisplayName("사용자 없음 → UserNotFoundException")
    void list_userNotFound() {
      Long userId = 1L;
      Long matchId = 10L;
      given(userRepository.findCurrentMatchIdById(userId))
          .willReturn(Optional.empty());

      assertThrows(UserNotFoundException.class, () ->
          service.list(userId, matchId, null, null, null, PageRequest.of(0, 20)));
    }

    @Test
    @DisplayName("현재 매치 불일치 → 403")
    void list_forbidden() {
      Long userId = 1L;
      Long matchId = 10L;
      given(userRepository.findCurrentMatchIdById(userId))
          .willReturn(Optional.of(999L));

      assertThrows(QuestionInstanceForbiddenException.class, () ->
          service.list(userId, matchId, InstanceStatus.COMPLETED,
              LocalDateTime.parse("2025-09-01T00:00:00"),
              LocalDateTime.parse("2025-09-30T23:59:59"),
              PageRequest.of(0, 20)));
    }

  }
}
