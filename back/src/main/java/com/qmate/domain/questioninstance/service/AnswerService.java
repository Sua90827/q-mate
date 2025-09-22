package com.qmate.domain.questioninstance.service;

import com.qmate.domain.questioninstance.entity.Answer;
import com.qmate.domain.questioninstance.entity.InstanceStatus;
import com.qmate.domain.questioninstance.entity.QuestionInstance;
import com.qmate.domain.questioninstance.mapper.AnswerMapper;
import com.qmate.domain.questioninstance.model.request.AnswerContentRequest;
import com.qmate.domain.questioninstance.model.response.AnswerResponse;
import com.qmate.domain.questioninstance.repository.AnswerRepository;
import com.qmate.domain.questioninstance.repository.QuestionInstanceRepository;
import com.qmate.domain.user.User;
import com.qmate.domain.user.UserRepository;
import com.qmate.exception.custom.UserNotFoundException;
import com.qmate.exception.custom.questioninstance.AnswerAlreadyExistsException;
import com.qmate.exception.custom.questioninstance.AnswerCannotModifyException;
import com.qmate.exception.custom.questioninstance.AnswerNotFoundException;
import com.qmate.exception.custom.questioninstance.QuestionInstanceForbiddenException;
import com.qmate.exception.custom.questioninstance.QuestionInstanceNotFoundException;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AnswerService {

  private final QuestionInstanceRepository questionInstanceRepository;
  private final UserRepository userRepository;
  private final AnswerRepository answerRepository;

  @Transactional
  public AnswerResponse create(Long questionInstanceId, Long userId, AnswerContentRequest req) {

    // 1) 로드
    QuestionInstance qi = questionInstanceRepository.findById(questionInstanceId)
        .orElseThrow(QuestionInstanceNotFoundException::new);

    User user = userRepository.findById(userId)
        .orElseThrow(UserNotFoundException::new);

    // 2) 권한 검증: 같은 매치인지 확인
    Long qiMatchId = qi.getMatch().getMatchId();
    Long userCurrentMatchId = user.getCurrentMatchId();
    if (qiMatchId == null || !qiMatchId.equals(userCurrentMatchId)) {
      throw new QuestionInstanceForbiddenException();
    }

    // 3) 상태 검증
    InstanceStatus status = qi.getStatus();
    // PENDING만 통과
    if (status != InstanceStatus.PENDING) {
      throw new AnswerCannotModifyException();
    }

    // 4) 저장 (+ 유니크 충돌을 409로 매핑)
    Answer saved;
    try {
      Answer entity = AnswerMapper.toEntity(qi, user, req);
      saved = answerRepository.save(entity);
    } catch (DataIntegrityViolationException e) {
      // (question_instance_id, user_id) 유니크 인덱스 충돌
      throw new AnswerAlreadyExistsException();
    }

    // 5) 두 사람 모두 답하면 QI 완료 전이
    if (answerRepository.countByQuestionInstance_Id(questionInstanceId) >= 2L) {
      qi.markCompleted(LocalDateTime.now());
      // TODO 알림 발송
    }

    // 6) 응답 매핑
    return AnswerMapper.toResponse(saved);
  }

  @Transactional
  public AnswerResponse update(Long answerId, Long userId, AnswerContentRequest req) {

    // 1) 로드
    Answer answer = answerRepository.findById(answerId)
        .orElseThrow(AnswerNotFoundException::new);

    // 2) 권한 검증: 작성자만 가능
    if (!answer.getUser().getId().equals(userId)) {
      throw new QuestionInstanceForbiddenException();
    }

    // 3) 상태 검증
    InstanceStatus status = answer.getQuestionInstance().getStatus();
    // PENDING만 통과
    if (status != InstanceStatus.PENDING) {
      throw new AnswerCannotModifyException();
    }

    // 4) 수정
    String normalized = AnswerMapper.normalize(req.getContent());
    answer.setContent(normalized);

    // 5) 응답 매핑
    return AnswerMapper.toResponse(answer);
  }
}
