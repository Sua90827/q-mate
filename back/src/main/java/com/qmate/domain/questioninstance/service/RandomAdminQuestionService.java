package com.qmate.domain.questioninstance.service;

import com.qmate.domain.match.Match;
import com.qmate.domain.question.entity.Question;
import com.qmate.domain.question.repository.QuestionRepository;
import com.qmate.domain.questioninstance.entity.QuestionInstance;
import com.qmate.domain.questioninstance.repository.QuestionInstanceRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RandomAdminQuestionService {

  private final QuestionRepository questionRepository;
  private final QuestionInstanceRepository questionInstanceRepository;

  public Optional<QuestionInstance> createOneRandomForMatch(Match match) {
    // 랜덤 후보 1건 조회 (미사용 + 활성 + 관계타입 일치)
    List<Question> picked = questionRepository
        .pickOneRandomUnusedAdminQuestion(match.getId(), PageRequest.of(0, 1));

    if (picked.isEmpty()) {
      return Optional.empty();
    }

    Question question = picked.getFirst();

    // 인스턴스 생성 및 저장
    QuestionInstance instance = QuestionInstance.builder()
        .match(match)
        .question(question)
        .build();

    QuestionInstance saved = questionInstanceRepository.save(instance);
    return Optional.of(saved);
  }
}
