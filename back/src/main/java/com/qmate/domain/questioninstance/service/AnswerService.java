package com.qmate.domain.questioninstance.service;

import com.qmate.domain.match.Match;
import com.qmate.domain.match.MatchMember;
import com.qmate.domain.match.repository.MatchMemberRepository;
import com.qmate.domain.pet.ExpReason;
import com.qmate.domain.pet.Pet;
import com.qmate.domain.pet.PetExpLog;
import com.qmate.domain.pet.repository.PetExpLogRepository;
import com.qmate.domain.pet.repository.PetRepository;
import com.qmate.domain.questioninstance.entity.Answer;
import com.qmate.domain.questioninstance.entity.QuestionInstance;
import com.qmate.domain.questioninstance.entity.QuestionInstanceStatus;
import com.qmate.domain.questioninstance.mapper.AnswerMapper;
import com.qmate.domain.questioninstance.model.request.AnswerContentRequest;
import com.qmate.domain.questioninstance.model.response.AnswerResponse;
import com.qmate.domain.questioninstance.repository.AnswerRepository;
import com.qmate.domain.questioninstance.repository.QuestionInstanceRepository;
import com.qmate.exception.custom.questioninstance.AnswerAlreadyExistsException;
import com.qmate.exception.custom.questioninstance.AnswerCannotModifyException;
import com.qmate.exception.custom.questioninstance.AnswerNotFoundException;
import com.qmate.exception.custom.questioninstance.QuestionInstanceNotFoundException;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AnswerService {

  private final QuestionInstanceRepository questionInstanceRepository;
  private final AnswerRepository answerRepository;
  private final MatchMemberRepository matchMemberRepository;
  private final PetRepository petRepository;
  private final PetExpLogRepository petExpLogRepository;

  @Transactional
  public AnswerResponse create(Long questionInstanceId, Long userId, AnswerContentRequest req) {

    // qi 와 user의 matchId를 기준으로 조건을 걸어서 만족한 qi 로드
    QuestionInstance qi = questionInstanceRepository.findAuthorizedByIdForUser(questionInstanceId,
            userId)
        .orElseThrow(QuestionInstanceNotFoundException::new);

    if (answerRepository.existsByQuestionInstance_IdAndUserId(questionInstanceId, userId)) {
      throw new AnswerAlreadyExistsException();
    }

    // 상태 검증
    QuestionInstanceStatus status = qi.getStatus();
    // PENDING만 통과
    if (status != QuestionInstanceStatus.PENDING) {
      throw new AnswerCannotModifyException();
    }

    // 저장
    Answer entity = AnswerMapper.toEntity(qi, req);
    Answer saved = answerRepository.save(entity);

    // matchMember의 lastAnsweredAt 갱신
    MatchMember matchMember = matchMemberRepository.findByMatch_IdAndUser_Id(qi.getMatch().getId(),
        userId).orElseThrow();
    matchMember.updateLastAnsweredAt();

    // 완료 전이 (QI 행 잠금)
    QuestionInstance locked = questionInstanceRepository.findByIdForUpdate(questionInstanceId)
        .orElseThrow();
    if (locked.getStatus() == QuestionInstanceStatus.PENDING &&
        answerRepository.countDistinctUserIdByQuestionInstance_Id(questionInstanceId) >= 2L) {
      locked.markCompleted(LocalDateTime.now());
    }
    addExperienceForAnswerCompletion(locked.getMatch());

    // 응답 매핑
    return AnswerMapper.toResponse(saved);
  }

  @Transactional
  public AnswerResponse update(Long answerId, Long userId, AnswerContentRequest req) {

    // 권한 확인과 동시에 조회
    Answer answer = answerRepository.findByIdAndUserId(answerId, userId)
        .orElseThrow(AnswerNotFoundException::new);

    // 상태 검증
    QuestionInstanceStatus status = answer.getQuestionInstance().getStatus();
    // PENDING만 통과
    if (status != QuestionInstanceStatus.PENDING) {
      throw new AnswerCannotModifyException();
    }

    // 4) 수정
    String normalized = AnswerMapper.normalize(req.getContent());
    answer.setContent(normalized);

    // matchMember의 lastAnsweredAt 갱신
    MatchMember matchMember = matchMemberRepository.findByMatch_IdAndUser_Id(
        answer.getQuestionInstance().getMatch().getId(), userId).orElseThrow();
    matchMember.updateLastAnsweredAt();

    // 5) updatedAt(@LastModifiedDate) 보장
    answerRepository.saveAndFlush(answer);

    // 6) 응답 매핑
    return AnswerMapper.toResponse(answer);
  }

  //답변 완료 시 펫 경험치를 추가하고 로그를 남기는 헬퍼 메서드
  private void addExperienceForAnswerCompletion(Match match) {
    // 해당 매칭의 펫을 찾습니다. 펫이 없다면 로직을 중단합니다.
    petRepository.findByMatch(match).ifPresent(pet -> {
      int experienceAmount = 10; // 획득 경험치는 상수로 관리하는 것이 좋습니다.
      // 펫의 경험치를 올립니다.
      pet.gainExperience(experienceAmount);
      // 경험치 획득 로그를 생성합니다.
      PetExpLog log = PetExpLog.builder()
          .match(match)
          .delta(experienceAmount)
          .reason(ExpReason.ANSWER_COMPLETED)
          .build();

      petExpLogRepository.save(log);
    });
  }
}
