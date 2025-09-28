package com.qmate.domain.quetioninstance.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.BDDAssertions.thenThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import com.qmate.domain.match.Match;
import com.qmate.domain.questioninstance.entity.Answer;
import com.qmate.domain.questioninstance.entity.QuestionInstanceStatus;
import com.qmate.domain.questioninstance.entity.QuestionInstance;
import com.qmate.domain.questioninstance.model.request.AnswerContentRequest;
import com.qmate.domain.questioninstance.repository.AnswerRepository;
import com.qmate.domain.questioninstance.repository.QuestionInstanceRepository;
import com.qmate.domain.questioninstance.service.AnswerService;
import com.qmate.domain.user.User;
import com.qmate.domain.user.UserRepository;
import com.qmate.exception.custom.matchinstance.UserNotFoundException;
import com.qmate.exception.custom.questioninstance.AnswerAlreadyExistsException;
import com.qmate.exception.custom.questioninstance.AnswerCannotModifyException;
import com.qmate.exception.custom.questioninstance.QuestionInstanceForbiddenException;
import com.qmate.exception.custom.questioninstance.QuestionInstanceNotFoundException;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

@ExtendWith(MockitoExtension.class)
class AnswerServiceCreateTest {
  @Mock
  QuestionInstanceRepository qiRepo;
  @Mock
  UserRepository userRepo;
  @Mock
  AnswerRepository answerRepo;

  @InjectMocks
  AnswerService sut;

  private QuestionInstance qi(Long qiId, Long matchId, QuestionInstanceStatus status) {
    return QuestionInstance.builder()
        .id(qiId)
        .status(status)
        .match(Match.builder().id(matchId).build())
        .build();
  }

  @Test
  @DisplayName("정상: 저장 후 두 사람 답변이면 QI 완료 전이")
  void create_정상_201() {
    // given
    Long qiId = 123L; Long userId = 99L; Long matchId = 7L;
    var req = new AnswerContentRequest("  안녕\r\n하세요  ");
    var user = User.builder().id(userId).currentMatchId(matchId).build();
    var qi = qi(qiId, matchId, QuestionInstanceStatus.PENDING);

    given(qiRepo.findById(qiId)).willReturn(Optional.of(qi));
    given(userRepo.findById(userId)).willReturn(Optional.of(user));

    // 저장 시 id/시간 세팅된 엔티티 리턴
    var saved = Answer.builder()
        .id(456L).questionInstance(qi).user(user)
        .content("안녕\n하세요").submittedAt(LocalDateTime.parse("2025-09-11T12:20:00")).build();
    given(answerRepo.save(any(Answer.class))).willReturn(saved);
    given(answerRepo.countByQuestionInstance_Id(qiId)).willReturn(2L);

    // when
    var res = sut.create(qiId, userId, req);

    // then
    assertThat(res.getAnswerId()).isEqualTo(456L);
    assertThat(res.getQuestionInstanceId()).isEqualTo(123L);
    assertThat(res.getContent()).isEqualTo("안녕\n하세요");
    assertThat(res.getSubmittedAt()).isEqualTo("2025-09-11T12:20:00");

    // 완료 전이 호출 확인(마커 메서드가 내부 상태만 바꾸면 상태만 검증)
    assertThat(qi.getStatus()).isEqualTo(QuestionInstanceStatus.COMPLETED);
  }

  @Test
  void create_권한불일치_403() {
    Long qiId = 1L; Long userId = 2L;
    var req = new AnswerContentRequest("a");
    var qi = qi(qiId, 100L, QuestionInstanceStatus.PENDING);
    var user = User.builder().id(userId).currentMatchId(200L).build();

    given(qiRepo.findById(qiId)).willReturn(Optional.of(qi));
    given(userRepo.findById(userId)).willReturn(Optional.of(user));

    assertThatThrownBy(() -> sut.create(qiId, userId, req))
        .isInstanceOf(QuestionInstanceForbiddenException.class);
  }

  @Test
  @DisplayName("만료(EXPIRED): 423 Locked")
  void create_expired_423() {
    Long qiId = 1L; Long userId = 2L; Long matchId = 7L;
    var req = new AnswerContentRequest("a");
    var qi = qi(qiId, matchId, QuestionInstanceStatus.EXPIRED);
    var user = User.builder().id(userId).currentMatchId(matchId).build();

    given(qiRepo.findById(qiId)).willReturn(Optional.of(qi));
    given(userRepo.findById(userId)).willReturn(Optional.of(user));

    assertThatThrownBy(() -> sut.create(qiId, userId, req))
        .isInstanceOf(AnswerCannotModifyException.class);
  }

  @Test
  @DisplayName("완료(COMPLETED): 423 Locked")
  void create_completed_423() {
    // given
    Long qiId = 1L, userId = 2L, matchId = 7L;
    var req = new AnswerContentRequest("a");
    var qi = qi(qiId, matchId, QuestionInstanceStatus.COMPLETED);
    var user = User.builder().id(userId).currentMatchId(matchId).build();

    given(qiRepo.findById(qiId)).willReturn(Optional.of(qi));
    given(userRepo.findById(userId)).willReturn(Optional.of(user));

    // expect
    thenThrownBy(() -> sut.create(qiId, userId, req))
        .isInstanceOf(AnswerCannotModifyException.class);
  }

  @Test
  void create_중복답변_409() {
    Long qiId = 1L; Long userId = 2L; Long matchId = 7L;
    var req = new AnswerContentRequest("a");
    var qi = qi(qiId, matchId, QuestionInstanceStatus.PENDING);
    var user = User.builder().id(userId).currentMatchId(matchId).build();

    given(qiRepo.findById(qiId)).willReturn(Optional.of(qi));
    given(userRepo.findById(userId)).willReturn(Optional.of(user));
    given(answerRepo.save(any(Answer.class)))
        .willThrow(new DataIntegrityViolationException("dup"));

    assertThatThrownBy(() -> sut.create(qiId, userId, req))
        .isInstanceOf(AnswerAlreadyExistsException.class);
  }

  @Test
  void create_QI없음_404_USER없음_404() {
    Long qiId = 1L; Long userId = 2L;
    given(qiRepo.findById(qiId)).willReturn(Optional.empty());
    assertThatThrownBy(() -> sut.create(qiId, userId, new AnswerContentRequest("a")))
        .isInstanceOf(QuestionInstanceNotFoundException.class);

    given(qiRepo.findById(qiId)).willReturn(Optional.of(qi(qiId, 7L, QuestionInstanceStatus.PENDING)));
    given(userRepo.findById(userId)).willReturn(Optional.empty());
    assertThatThrownBy(() -> sut.create(qiId, userId, new AnswerContentRequest("a")))
        .isInstanceOf(UserNotFoundException.class);
  }
}
