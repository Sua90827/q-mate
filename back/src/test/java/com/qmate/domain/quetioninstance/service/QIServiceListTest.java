package com.qmate.domain.quetioninstance.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.BDDMockito.given;

import com.qmate.domain.questioninstance.model.response.QIListItem;
import com.qmate.domain.questioninstance.repository.QuestionInstanceRepository;
import com.qmate.domain.questioninstance.service.QuestionInstanceService;
import com.qmate.domain.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
      Page<QIListItem> expected = Page.empty(PageRequest.of(0, 20, Sort.by("deliveredAt").descending()));
      given(qiRepository.findPageByMatchIdForRequesterWithQuestion(eq(matchId), eq(userId), isNull(), isNull(), isNull(), any(Pageable.class)))
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

}
