package com.qmate.domain.quetioninstance.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import com.qmate.domain.match.Match;
import com.qmate.domain.match.MatchMember;
import com.qmate.domain.questioninstance.entity.Answer;
import com.qmate.domain.questioninstance.entity.QuestionInstance;
import com.qmate.domain.questioninstance.entity.QuestionInstanceStatus;
import com.qmate.domain.questioninstance.mapper.QIDetailMapper;
import com.qmate.domain.questioninstance.model.response.QIDetailResponse;
import com.qmate.domain.questioninstance.repository.AnswerRepository;
import com.qmate.domain.questioninstance.repository.QuestionInstanceRepository;
import com.qmate.domain.questioninstance.service.QuestionInstanceService;
import com.qmate.domain.user.User;
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

  @Mock
  QuestionInstanceRepository qiRepository;
  @Mock
  AnswerRepository answerRepository;

  @BeforeEach
  void setUp() {
    service = new QuestionInstanceService(qiRepository, answerRepository);
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
          .status(QuestionInstanceStatus.PENDING)
          .build();
      User me = User.builder().id(meId).currentMatchId(matchId).build();
      User partner = User.builder().id(partnerId).currentMatchId(matchId).build();
      MatchMember mmMe = MatchMember.builder().match(match).user(me).build();
      MatchMember mmPartner = MatchMember.builder().match(match).user(partner).build();
      match.getMembers().add(mmMe);
      match.getMembers().add(mmPartner);

      when(qiRepository.findDetailWithMatchMembersAndQuestionByIdIfRequesterInMatch(qiId, meId)).thenReturn(Optional.of(qi));

      Answer myAnswer = Answer.builder().id(1L).userId(me.getId()).questionInstance(qi).build();
      when(answerRepository.findByQuestionInstance_IdAndUserId(qiId, meId))
          .thenReturn(Optional.of(myAnswer));
      when(answerRepository.findByQuestionInstance_IdAndUserId(qiId, partnerId))
          .thenReturn(Optional.empty());

      QIDetailResponse expected = QIDetailResponse.builder()
          .questionInstanceId(qiId)
          .matchId(matchId)
          .status(QuestionInstanceStatus.PENDING)
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
          .status(QuestionInstanceStatus.COMPLETED)
          .build();
      User me = User.builder().id(meId).currentMatchId(matchId).build();
      User partner = User.builder().id(partnerId).currentMatchId(matchId).build();
      MatchMember mmMe = MatchMember.builder().match(match).user(me).build();
      MatchMember mmPartner = MatchMember.builder().match(match).user(partner).build();
      match.getMembers().add(mmMe);
      match.getMembers().add(mmPartner);

      when(qiRepository.findDetailWithMatchMembersAndQuestionByIdIfRequesterInMatch(qiId, meId)).thenReturn(Optional.of(qi));

      Answer myAnswer = Answer.builder().id(1L).userId(me.getId()).questionInstance(qi).build();
      Answer partnerAnswer = Answer.builder().id(2L).userId(partner.getId()).questionInstance(qi).build();
      when(answerRepository.findByQuestionInstance_IdAndUserId(qiId, meId))
          .thenReturn(Optional.of(myAnswer));
      when(answerRepository.findByQuestionInstance_IdAndUserId(qiId, partnerId))
          .thenReturn(Optional.of(partnerAnswer));

      QIDetailResponse expected = QIDetailResponse.builder()
          .questionInstanceId(qiId)
          .matchId(matchId)
          .status(QuestionInstanceStatus.COMPLETED)
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
          .status(QuestionInstanceStatus.EXPIRED)
          .build();
      User me = User.builder().id(meId).currentMatchId(matchId).build();
      User partner = User.builder().id(partnerId).currentMatchId(matchId).build();
      MatchMember mmMe = MatchMember.builder().match(match).user(me).build();
      MatchMember mmPartner = MatchMember.builder().match(match).user(partner).build();
      match.getMembers().add(mmMe);
      match.getMembers().add(mmPartner);

      when(qiRepository.findDetailWithMatchMembersAndQuestionByIdIfRequesterInMatch(qiId, meId)).thenReturn(Optional.of(qi));

      when(answerRepository.findByQuestionInstance_IdAndUserId(qiId, meId))
          .thenReturn(Optional.empty());
      when(answerRepository.findByQuestionInstance_IdAndUserId(qiId, partnerId))
          .thenReturn(Optional.empty());

      QIDetailResponse expected = QIDetailResponse.builder()
          .questionInstanceId(qiId)
          .matchId(matchId)
          .status(QuestionInstanceStatus.EXPIRED)
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
      when(qiRepository.findDetailWithMatchMembersAndQuestionByIdIfRequesterInMatch(999L, 1L)).thenReturn(Optional.empty());

      try {
        service.getDetail(999L, 1L);
      } catch (Exception e) {
        assertThat(e).isInstanceOf(QuestionInstanceNotFoundException.class);
      }
    }

  }
}
