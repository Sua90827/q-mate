package com.qmate.domain.quetioninstance.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.*;

import com.qmate.domain.questioninstance.entity.Answer;
import com.qmate.domain.questioninstance.entity.QuestionInstanceStatus;
import com.qmate.domain.questioninstance.entity.QuestionInstance;
import com.qmate.domain.questioninstance.model.request.AnswerContentRequest;
import com.qmate.domain.questioninstance.model.response.AnswerResponse;
import com.qmate.domain.questioninstance.repository.AnswerRepository;
import com.qmate.domain.questioninstance.service.AnswerService;
import com.qmate.domain.user.User;
import com.qmate.exception.custom.questioninstance.AnswerCannotModifyException;
import com.qmate.exception.custom.questioninstance.AnswerForbiddenException;
import com.qmate.exception.custom.questioninstance.AnswerNotFoundException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AnswerServiceUpdateTest {

  @Mock
  AnswerRepository answerRepository;
  @InjectMocks
  AnswerService answerService;

  @Nested
  @DisplayName("정상 작동")
  class Success {

    @Test
    @DisplayName("작성자 본인이 PENDING 상태에서 내용 수정 → 응답 스펙/정규화 검증")
    void update_ok() {
      // given
      Long answerId = 100L;
      Long userId = 1L;

      User owner = User.builder()
          .id(userId)
          .email("owner@test.com")
          .nickname("owner")
          .passwordHash("x")
          .birthDate(LocalDate.now())
          .build();

      QuestionInstance qi = QuestionInstance.builder()
          .id(10L)
          .status(QuestionInstanceStatus.PENDING)
          .build();

      Answer answer = Answer.builder()
          .id(answerId)
          .user(owner)
          .questionInstance(qi)
          .content("초기 내용")
          .submittedAt(LocalDateTime.parse("2025-09-11T12:00:00"))
          .updatedAt(LocalDateTime.parse("2025-09-11T12:30:00"))
          .build();

      given(answerRepository.findById(answerId)).willReturn(Optional.of(answer));

      String raw = "  수정된 내용\r\n두줄  ";
      String normalized = "수정된 내용\n두줄";

      // when
      AnswerResponse res = answerService.update(answerId, userId, new AnswerContentRequest(raw));

      // then: 리포지토리 호출 검증
      then(answerRepository).should(times(1)).findById(answerId);
      then(answerRepository).should(times(1)).saveAndFlush(answer);
      then(answerRepository).shouldHaveNoMoreInteractions();

      // 엔티티 자체가 수정되었는지(정규화 반영) + 응답 스펙 확인
      assertThat(answer.getContent()).isEqualTo(normalized);
      assertThat(res.getAnswerId()).isEqualTo(answerId);
      assertThat(res.getQuestionInstanceId()).isEqualTo(qi.getId());
      assertThat(res.getContent()).isEqualTo(normalized);
      assertThat(res.getSubmittedAt()).isEqualTo(answer.getSubmittedAt());
      assertThat(res.getUpdatedAt()).isNotNull();
    }
  }

  @Nested
  @DisplayName("실패")
  class Failures {

    @Test
    @DisplayName("404 Not Found: 미존재 answerId")
    void update_notFound_404() {
      // given
      Long notExist = 999L;
      given(answerRepository.findById(notExist)).willReturn(Optional.empty());

      // when / then
      assertThrows(AnswerNotFoundException.class,
          () -> answerService.update(notExist, 1L, new AnswerContentRequest("x")));

      then(answerRepository).should(times(1)).findById(notExist);
      then(answerRepository).shouldHaveNoMoreInteractions();
    }

    @Test
    @DisplayName("403 Forbidden: 작성자 아님")
    void update_forbidden_403() {
      // given
      Long answerId = 100L;
      Long ownerId = 1L;
      Long otherId = 2L;

      User owner = User.builder()
          .id(ownerId).email("o@o").nickname("o")
          .passwordHash("x").birthDate(LocalDate.now())
          .build();

      QuestionInstance qi = QuestionInstance.builder()
          .id(10L).status(QuestionInstanceStatus.PENDING).build();

      Answer answer = Answer.builder()
          .id(answerId).user(owner).questionInstance(qi).content("초기").build();

      given(answerRepository.findById(answerId)).willReturn(Optional.of(answer));

      // when / then
      assertThrows(AnswerForbiddenException.class,
          () -> answerService.update(answerId, otherId, new AnswerContentRequest("x")));

      then(answerRepository).should(times(1)).findById(answerId);
      then(answerRepository).shouldHaveNoMoreInteractions();
    }

    @Test
    @DisplayName("423 Locked: PENDING 이외 상태")
    void update_locked_423() {
      // given
      Long answerId = 100L;
      Long userId = 1L;

      User owner = User.builder()
          .id(userId).email("o@o").nickname("o")
          .passwordHash("x").birthDate(LocalDate.now())
          .build();

      QuestionInstance qi = QuestionInstance.builder()
          .id(10L).status(QuestionInstanceStatus.COMPLETED) // 수정 불가 상태
          .build();

      Answer answer = Answer.builder()
          .id(answerId).user(owner).questionInstance(qi).content("초기").build();

      given(answerRepository.findById(answerId)).willReturn(Optional.of(answer));

      // when / then
      assertThrows(AnswerCannotModifyException.class,
          () -> answerService.update(answerId, userId, new AnswerContentRequest("x")));

      then(answerRepository).should(times(1)).findById(answerId);
      then(answerRepository).shouldHaveNoMoreInteractions();
    }

  }

}
