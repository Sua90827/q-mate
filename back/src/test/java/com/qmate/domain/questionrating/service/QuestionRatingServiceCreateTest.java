package com.qmate.domain.questionrating.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.qmate.common.redis.rating.RedisQuestionRatingCounter;
import com.qmate.domain.question.entity.Question;
import com.qmate.domain.question.repository.QuestionRepository;
import com.qmate.domain.questionrating.entity.QuestionRating;
import com.qmate.domain.questionrating.model.request.QuestionRatingRequest;
import com.qmate.domain.questionrating.model.response.QuestionRatingResponse;
import com.qmate.domain.questionrating.repository.QuestionRatingRepository;
import com.qmate.exception.BusinessGlobalException;
import com.qmate.exception.CommonErrorCode;
import com.qmate.exception.custom.question.DuplicateQuestionRatingException;
import com.qmate.exception.custom.question.QuestionNotFoundException;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.AuditorAware;

@ExtendWith(MockitoExtension.class)
class QuestionRatingServiceCreateTest {

  @Mock
  QuestionRepository questionRepository;
  @Mock
  QuestionRatingRepository questionRatingRepository;
  @Mock
  RedisQuestionRatingCounter redisQuestionRatingCounter;
  @Mock
  AuditorAware<Long> auditorAware;

  @InjectMocks
  QuestionRatingService service;

  @Test
  @DisplayName("201 : 정상 생성 시 DTO가 기대값으로 반환된다")
  void create_success() {
    // given
    Long qid = 777L;
    Long uid = 99L;

    // 엔티티는 전부 빌더로 생성
    Question question = Question.builder()
        .id(qid)
        .build();

    given(questionRepository.findById(qid)).willReturn(Optional.of(question));
    given(auditorAware.getCurrentAuditor()).willReturn(Optional.of(uid));
    given(questionRatingRepository.existsByQuestion_IdAndUserId(qid, uid)).willReturn(false);

    // save 후 반환될 엔티티 (빌더)
    QuestionRating saved = QuestionRating.builder()
        .id(890L)
        .question(question)
        .userId(uid)
        .isLike(true)
        .createdAt(LocalDateTime.of(2025, 9, 11, 13, 30))
        .build();

    given(questionRatingRepository.save(any(QuestionRating.class))).willReturn(saved);

    QuestionRatingRequest req = new QuestionRatingRequest(true);

    // when
    QuestionRatingResponse res = service.create(qid, req);

    // then
    assertThat(res.getRatingId()).isEqualTo(890L);
    assertThat(res.getQuestionId()).isEqualTo(777L);
    assertThat(res.getUserId()).isEqualTo(99L);
    assertThat(res.isLike()).isTrue();
    assertThat(res.getCreatedAt()).isEqualTo(LocalDateTime.of(2025, 9, 11, 13, 30));

    then(questionRepository).should().findById(qid);
    then(auditorAware).should().getCurrentAuditor();
    then(questionRatingRepository).should().existsByQuestion_IdAndUserId(qid, uid);
    then(questionRatingRepository).should().save(any(QuestionRating.class));
  }

  @Test
  @DisplayName("404 : 질문이 없으면 QuestionNotFoundException")
  void create_fail_whenQuestionNotFound() {
    // given
    Long qid = 777L;
    given(questionRepository.findById(qid)).willReturn(Optional.empty());

    QuestionRatingRequest req = new QuestionRatingRequest(true);

    // expect
    assertThatThrownBy(() -> service.create(qid, req))
        .isInstanceOf(QuestionNotFoundException.class);
  }

  @Test
  @DisplayName("401 : Auditor가 비어있으면 BusinessGlobalException(unauthorized)")
  void create_fail_whenUnauthorized() {
    // given
    Long qid = 777L;

    Question question = Question.builder().id(qid).build();

    given(questionRepository.findById(qid)).willReturn(Optional.of(question));
    given(auditorAware.getCurrentAuditor()).willReturn(Optional.empty());

    QuestionRatingRequest req = new QuestionRatingRequest(true);

    // expect
    assertThatThrownBy(() -> service.create(qid, req))
        .isInstanceOf(BusinessGlobalException.class)
        .hasMessageContaining(CommonErrorCode.unauthorized().getMessage());
  }

  @Test
  @DisplayName("409 : 동일 사용자가 이미 평가했다면 DuplicateQuestionRatingException")
  void create_fail_whenDuplicate() {
    // given
    Long qid = 777L;
    Long uid = 99L;

    Question question = Question.builder().id(qid).build();

    given(questionRepository.findById(qid)).willReturn(Optional.of(question));
    given(auditorAware.getCurrentAuditor()).willReturn(Optional.of(uid));
    given(questionRatingRepository.existsByQuestion_IdAndUserId(qid, uid)).willReturn(true);

    QuestionRatingRequest req = new QuestionRatingRequest(true);

    // expect
    assertThatThrownBy(() -> service.create(qid, req))
        .isInstanceOf(DuplicateQuestionRatingException.class);
  }
}
