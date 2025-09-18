package com.qmate.domain.question.service;

import com.qmate.common.constants.question.QuestionConstants;
import com.qmate.domain.match.Match;
import com.qmate.domain.question.entity.CustomQuestion;
import com.qmate.domain.question.entity.RelationType;
import com.qmate.domain.question.mapper.QuestionMapper;
import com.qmate.domain.question.model.request.CustomQuestionTextRequest;
import com.qmate.domain.question.model.response.CustomQuestionResponse;
import com.qmate.domain.question.repository.CustomQuestionRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CustomQuestionService {

  private final CustomQuestionRepository customQuestionRepository;

  /**
   * 커스텀 질문 생성
   *
   * @param userId  요청 사용자 ID
   * @param matchId 질문이 속할 Match ID
   * @param request 질문 생성 요청
   * @return 생성된 커스텀 질문 정보
   */
  @Transactional
  public CustomQuestionResponse create(Long userId, Long matchId, CustomQuestionTextRequest request) {

    // Match 존재 여부 확인 및 로드
    Match match = loadMatchOrThrow(matchId);

    // TODO User-Match 권한 확인

    CustomQuestion saved = customQuestionRepository.save(
        QuestionMapper.toEntity(match, request)
    );

    boolean editable = true; // 생성된 질문은 항상 수정 가능
    return QuestionMapper.toResponse(saved, editable, match);
  }

  /**
   * 커스텀 질문 수정
   *
   * @param userId  요청 사용자 ID
   * @param id      수정할 커스텀 질문 ID
   * @param request 질문 수정 요청
   * @return 수정된 커스텀 질문 정보
   */
  @Transactional
  public CustomQuestionResponse update(Long userId, Long id, CustomQuestionTextRequest request) {
    CustomQuestion entity = loadWithMatchOrThrow(id);

    // 본인 질문인지 확인
    if (!Objects.equals(entity.getCreatedBy().getId(), userId)) {
      // TODO 커스텀 예외 처리
      throw new RuntimeException(QuestionConstants.CUSTOM_QUESTION_FORBIDDEN_MESSAGE);
    }

    // TODO 수정 가능한 상태인지 확인 : QuestionInstance 존재 여부로 판단


    entity.setText(request.getText().trim());

    boolean editable = true; // 수정 시점에는 항상 수정 가능
    return QuestionMapper.toResponse(entity, editable, entity.getMatch());
  }

  /**
   * 커스텀 질문 삭제
   *
   * @param userId 요청 사용자 ID
   * @param id     삭제할 커스텀 질문 ID
   */
  @Transactional
  public void delete(Long userId, Long id) {
    CustomQuestion entity = loadOrThrow(id);
    // 본인 질문인지 확인
    if (!Objects.equals(entity.getCreatedBy().getId(), userId)) {
      // TODO 커스텀 예외 처리
      throw new RuntimeException(QuestionConstants.CUSTOM_QUESTION_FORBIDDEN_MESSAGE);
    }
    // TODO 삭제 가능한 상태인지 확인 : QuestionInstance 존재 여부로 판단
    customQuestionRepository.delete(entity);
  }

  /**
   * 커스텀 질문 단건 조회
   *
   * @param id 조회할 커스텀 질문 ID
   * @return 커스텀 질문 정보
   */
  public CustomQuestionResponse getOne(Long userId, Long id) {
    CustomQuestion entity = loadWithMatchOrThrow(id);
    // 본인 질문인지 확인
    if (!Objects.equals(entity.getCreatedBy().getId(), userId)) {
      // TODO 커스텀 예외 처리
      throw new RuntimeException(QuestionConstants.CUSTOM_QUESTION_FORBIDDEN_MESSAGE);
    }
    boolean editable = true; // TODO QuestionInstance 존재 여부로 판단
    return QuestionMapper.toResponse(entity, editable, entity.getMatch());
  }

  private Match loadMatchOrThrow(Long matchId) {
    // TODO MatchRepository 연동
    return null; // 임시 반환
  }

  private CustomQuestion loadOrThrow(Long id) {
    return customQuestionRepository.findById(id)
        // TODO 커스텀 예외 처리
        .orElseThrow(() -> new EntityNotFoundException(QuestionConstants.CUSTOM_QUESTION_NOT_FOUND_MESSAGE));
  }

  private CustomQuestion loadWithMatchOrThrow(Long id) {
    return customQuestionRepository.findWithMatchById(id)
        // TODO 커스텀 예외 처리
        .orElseThrow(() -> new EntityNotFoundException(QuestionConstants.CUSTOM_QUESTION_NOT_FOUND_MESSAGE));
  }
}
