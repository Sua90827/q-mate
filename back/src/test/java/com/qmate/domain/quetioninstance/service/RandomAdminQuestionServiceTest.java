package com.qmate.domain.quetioninstance.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.never;

import com.qmate.domain.match.Match;
import com.qmate.domain.question.entity.Question;
import com.qmate.domain.question.repository.QuestionRepository;
import com.qmate.domain.questioninstance.entity.QuestionInstance;
import com.qmate.domain.questioninstance.repository.QuestionInstanceRepository;
import com.qmate.domain.questioninstance.service.RandomAdminQuestionService;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class RandomAdminQuestionServiceTest {

  @Mock private QuestionRepository questionRepository;
  @Mock private QuestionInstanceRepository questionInstanceRepository;

  @InjectMocks private RandomAdminQuestionService service;

  @Test
  void createOneRandomForMatch_candidateExists_savesAndReturnsInstance() {
    // given
    Match match = mock(Match.class);
    given(match.getId()).willReturn(10L);

    Question q = mock(Question.class);
    given(questionRepository.pickOneRandomUnusedAdminQuestion(eq(10L), eq(PageRequest.of(0, 1))))
        .willReturn(List.of(q));

    // save -> 그대로 반환 (id 설정 없어도 무방)
    willAnswer(invocation -> invocation.getArgument(0))
        .given(questionInstanceRepository).save(any(QuestionInstance.class));

    // when
    Optional<QuestionInstance> result = service.createOneRandomForMatch(match);

    // then
    assertThat(result).isPresent();
    then(questionInstanceRepository).should().save(argThat(inst ->
        inst.getMatch() == match && inst.getQuestion() == q
    ));
  }

  @Test
  void createOneRandomForMatch_noCandidate_doesNotSave_returnsEmpty() {
    // given
    Match match = mock(Match.class);
    given(match.getId()).willReturn(20L);
    given(questionRepository.pickOneRandomUnusedAdminQuestion(eq(20L), eq(PageRequest.of(0, 1))))
        .willReturn(List.of());

    // when
    Optional<QuestionInstance> result = service.createOneRandomForMatch(match);

    // then
    assertThat(result).isEmpty();
    then(questionInstanceRepository).should(never()).save(any());
  }
}
