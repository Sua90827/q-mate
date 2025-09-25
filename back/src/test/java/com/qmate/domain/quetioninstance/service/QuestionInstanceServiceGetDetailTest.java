package com.qmate.domain.quetioninstance.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import com.qmate.domain.match.Match;
import com.qmate.domain.questioninstance.entity.Answer;
import com.qmate.domain.questioninstance.entity.InstanceStatus;
import com.qmate.domain.questioninstance.entity.QuestionInstance;
import com.qmate.domain.questioninstance.mapper.QIDetailMapper;
import com.qmate.domain.questioninstance.model.response.QIDetailResponse;
import com.qmate.domain.questioninstance.repository.AnswerRepository;
import com.qmate.domain.questioninstance.repository.QuestionInstanceRepository;
import com.qmate.domain.questioninstance.service.QuestionInstanceService;
import com.qmate.domain.user.User;
import com.qmate.domain.user.UserRepository;
import com.qmate.exception.custom.matchinstance.UserNotFoundException;
import com.qmate.exception.custom.questioninstance.QuestionInstanceForbiddenException;
import com.qmate.exception.custom.questioninstance.QuestionInstanceNotFoundException;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class QuestionInstanceServiceGetDetailTest {

  private QuestionInstanceService service;

  @Mock QuestionInstanceRepository qiRepository;
  @Mock AnswerRepository answerRepository;
  @Mock UserRepository userRepository;

  @BeforeEach
  void setUp() {
    service = new QuestionInstanceService(qiRepository, answerRepository, userRepository);
  }

  @Nested
  class HappyPaths {

    @Test
    @DisplayName("PENDING: 내 답변만 공개, 상대는 마스킹")
    void pending_onlyMineVisible() {
      Long qiId = 10L;
      Long meId = 99L;
      Long partnerId = 100L;
      Long matchId = 1L;

      Match match = Match.builder().id(matchId).build();
      QuestionInstance qi = QuestionInstance.builder()
          .id(qiId)
          .match(match)
          .status(InstanceStatus.PENDING)
          .build();
      User me = User.builder().id(meId).currentMatchId(matchId).build();
      User partner = User.builder().id(partnerId).currentMatchId(matchId).build();

      when(qiRepository.findDetailWithQuestionAndMatch(qiId)).thenReturn(Optional.of(qi));
      when(userRepository.findById(meId)).thenReturn(Optional.of(me));
      when(userRepository.findByCurrentMatchIdAndIdNot(matchId, meId)).thenReturn(Optional.of(partner));

      Answer myAnswer = Answer.builder().id(1L).user(me).questionInstance(qi).build();
      when(answerRepository.findByQuestionInstance_IdAndUser_Id(qiId, meId))
          .thenReturn(Optional.of(myAnswer));
      when(answerRepository.findByQuestionInstance_IdAndUser_Id(qiId, partnerId))
          .thenReturn(Optional.empty());

      QIDetailResponse expected = QIDetailResponse.builder()
          .questionInstanceId(qiId)
          .matchId(matchId)
          .status(InstanceStatus.PENDING)
          .build();

      try (MockedStatic<QIDetailMapper> mocked = mockStatic(QIDetailMapper.class)) {
        mocked.when(() -> QIDetailMapper.toResponse(
            same(qi), same(match), same(me), same(partner),
            same(myAnswer), isNull(), eq(true), eq(false)
        )).thenReturn(expected);

        QIDetailResponse actual = service.getDetail(qiId, meId);
        assertThat(actual).isSameAs(expected);
      }
    }

    @Test
    @DisplayName("COMPLETED: 양쪽 답변 공개")
    void completed_bothVisible() {
      Long qiId = 11L;
      Long meId = 99L;
      Long partnerId = 100L;
      Long matchId = 2L;

      Match match = Match.builder().id(matchId).build();
      QuestionInstance qi = QuestionInstance.builder()
          .id(qiId)
          .match(match)
          .status(InstanceStatus.COMPLETED)
          .build();
      User me = User.builder().id(meId).currentMatchId(matchId).build();
      User partner = User.builder().id(partnerId).currentMatchId(matchId).build();

      when(qiRepository.findDetailWithQuestionAndMatch(qiId)).thenReturn(Optional.of(qi));
      when(userRepository.findById(meId)).thenReturn(Optional.of(me));
      when(userRepository.findByCurrentMatchIdAndIdNot(matchId, meId)).thenReturn(Optional.of(partner));

      Answer myAnswer = Answer.builder().id(1L).user(me).questionInstance(qi).build();
      Answer partnerAnswer = Answer.builder().id(2L).user(partner).questionInstance(qi).build();
      when(answerRepository.findByQuestionInstance_IdAndUser_Id(qiId, meId))
          .thenReturn(Optional.of(myAnswer));
      when(answerRepository.findByQuestionInstance_IdAndUser_Id(qiId, partnerId))
          .thenReturn(Optional.of(partnerAnswer));

      QIDetailResponse expected = QIDetailResponse.builder()
          .questionInstanceId(qiId)
          .matchId(matchId)
          .status(InstanceStatus.COMPLETED)
          .build();

      try (MockedStatic<QIDetailMapper> mocked = mockStatic(QIDetailMapper.class)) {
        mocked.when(() -> QIDetailMapper.toResponse(
            same(qi), same(match), same(me), same(partner),
            same(myAnswer), same(partnerAnswer), eq(true), eq(true)
        )).thenReturn(expected);

        QIDetailResponse actual = service.getDetail(qiId, meId);
        assertThat(actual).isSameAs(expected);
      }
    }

    @Test
    @DisplayName("EXPIRED: 내/상대 답변 모두 비공개(또는 null)")
    void expired_bothHidden() {
      Long qiId = 12L;
      Long meId = 99L;
      Long partnerId = 100L;
      Long matchId = 3L;

      Match match = Match.builder().id(matchId).build();
      QuestionInstance qi = QuestionInstance.builder()
          .id(qiId)
          .match(match)
          .status(InstanceStatus.EXPIRED)
          .build();
      User me = User.builder().id(meId).currentMatchId(matchId).build();
      User partner = User.builder().id(partnerId).currentMatchId(matchId).build();

      when(qiRepository.findDetailWithQuestionAndMatch(qiId)).thenReturn(Optional.of(qi));
      when(userRepository.findById(meId)).thenReturn(Optional.of(me));
      when(userRepository.findByCurrentMatchIdAndIdNot(matchId, meId)).thenReturn(Optional.of(partner));

      when(answerRepository.findByQuestionInstance_IdAndUser_Id(qiId, meId))
          .thenReturn(Optional.empty());
      when(answerRepository.findByQuestionInstance_IdAndUser_Id(qiId, partnerId))
          .thenReturn(Optional.empty());

      QIDetailResponse expected = QIDetailResponse.builder()
          .questionInstanceId(qiId)
          .matchId(matchId)
          .status(InstanceStatus.EXPIRED)
          .build();

      try (MockedStatic<QIDetailMapper> mocked = mockStatic(QIDetailMapper.class)) {
        mocked.when(() -> QIDetailMapper.toResponse(
            same(qi), same(match), same(me), same(partner),
            isNull(), isNull(), eq(false), eq(false)
        )).thenReturn(expected);

        QIDetailResponse actual = service.getDetail(qiId, meId);
        assertThat(actual).isSameAs(expected);
      }
    }
  }

  @Nested
  class ErrorCases {

    @Test
    @DisplayName("QI가 없으면 404")
    void qiNotFound() {
      when(qiRepository.findDetailWithQuestionAndMatch(999L)).thenReturn(Optional.empty());

      try {
        service.getDetail(999L, 1L);
      } catch (Exception e) {
        assertThat(e).isInstanceOf(QuestionInstanceNotFoundException.class);
      }
    }

    @Test
    @DisplayName("요청자(User) 없으면 404")
    void userNotFound() {
      Long qiId = 1L, matchId = 10L;

      Match match = Match.builder().id(matchId).build();
      QuestionInstance qi = QuestionInstance.builder()
          .id(qiId)
          .match(match)
          .status(InstanceStatus.PENDING)
          .build();

      when(qiRepository.findDetailWithQuestionAndMatch(qiId)).thenReturn(Optional.of(qi));
      when(userRepository.findById(123L)).thenReturn(Optional.empty());

      try {
        service.getDetail(qiId, 123L);
      } catch (Exception e) {
        assertThat(e).isInstanceOf(UserNotFoundException.class);
      }
    }

    @Test
    @DisplayName("권한 불일치면 403")
    void forbidden_hiddenAsNotFound() {
      Long qiId = 2L, matchId = 20L;

      Match match = Match.builder().id(matchId).build();
      QuestionInstance qi = QuestionInstance.builder()
          .id(qiId)
          .match(match)
          .status(InstanceStatus.PENDING)
          .build();

      User me = User.builder().id(7L).currentMatchId(21L).build(); // mismatch

      when(qiRepository.findDetailWithQuestionAndMatch(qiId)).thenReturn(Optional.of(qi));
      when(userRepository.findById(7L)).thenReturn(Optional.of(me));

      try {
        service.getDetail(qiId, 7L);
      } catch (Exception e) {
        assertThat(e).isInstanceOf(QuestionInstanceForbiddenException.class);
      }
    }
  }
}
