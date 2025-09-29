package com.qmate.domain.quetioninstance.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.times;

import com.qmate.domain.match.Match;
import com.qmate.domain.match.MatchMember;
import com.qmate.domain.match.RelationType;
import com.qmate.domain.match.repository.MatchMemberRepository;
import com.qmate.domain.questioninstance.entity.Answer;
import com.qmate.domain.questioninstance.entity.QuestionInstance;
import com.qmate.domain.questioninstance.entity.QuestionInstanceStatus;
import com.qmate.domain.questioninstance.model.request.AnswerContentRequest;
import com.qmate.domain.questioninstance.model.response.AnswerResponse;
import com.qmate.domain.questioninstance.repository.AnswerRepository;
import com.qmate.domain.questioninstance.service.AnswerService;
import com.qmate.domain.user.User;
import com.qmate.exception.custom.questioninstance.AnswerCannotModifyException;
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

  @Mock
  MatchMemberRepository matchMemberRepository;

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

      Match match = Match.builder()
          .id(11L)
          .relationType(RelationType.COUPLE)
          .build();

      QuestionInstance qi = QuestionInstance.builder()
          .id(10L)
          .match(match)
          .status(QuestionInstanceStatus.PENDING)
          .build();

      Answer answer = Answer.builder()
          .id(answerId)
          .userId(owner.getId())
          .questionInstance(qi)
          .content("초기 내용")
          .submittedAt(LocalDateTime.parse("2025-09-11T12:00:00"))
          .updatedAt(LocalDateTime.parse("2025-09-11T12:30:00"))
          .build();

      MatchMember matchMember = MatchMember.builder()
          .id(100L)
          .match(match)
          .user(owner)
          .build();

      given(answerRepository.findByIdAndUserId(answerId, userId)).willReturn(Optional.of(answer));
      given(matchMemberRepository.findByMatch_IdAndUser_Id(qi.getMatch().getId(), userId)).willReturn(Optional.of(matchMember));

      String raw = "  수정된 내용\r\n두줄  ";
      String normalized = "수정된 내용\n두줄";

      // when
      AnswerResponse res = answerService.update(answerId, userId, new AnswerContentRequest(raw));

      // then: 리포지토리 호출 검증
      then(answerRepository).should(times(1)).findByIdAndUserId(answerId, userId);
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
      given(answerRepository.findByIdAndUserId(notExist, 1L)).willReturn(Optional.empty());

      // when / then
      assertThrows(AnswerNotFoundException.class,
          () -> answerService.update(notExist, 1L, new AnswerContentRequest("x")));

      then(answerRepository).should(times(1)).findByIdAndUserId(notExist, 1L);
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
          .id(answerId).userId(owner.getId()).questionInstance(qi).content("초기").build();

      given(answerRepository.findByIdAndUserId(answerId, userId)).willReturn(Optional.of(answer));

      // when / then
      assertThrows(AnswerCannotModifyException.class,
          () -> answerService.update(answerId, userId, new AnswerContentRequest("x")));

      then(answerRepository).should(times(1)).findByIdAndUserId(answerId, userId);
      then(answerRepository).shouldHaveNoMoreInteractions();
    }

  }

}
