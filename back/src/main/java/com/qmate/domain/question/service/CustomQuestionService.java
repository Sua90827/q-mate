package com.qmate.domain.question.service;

import com.qmate.domain.match.Match;
import com.qmate.domain.match.repository.MatchRepository;
import com.qmate.domain.question.entity.CustomQuestion;
import com.qmate.domain.question.mapper.QuestionMapper;
import com.qmate.domain.question.model.request.CustomQuestionStatusFilter;
import com.qmate.domain.question.model.request.CustomQuestionTextRequest;
import com.qmate.domain.question.model.response.CustomQuestionResponse;
import com.qmate.domain.question.repository.CustomQuestionRepository;
import com.qmate.domain.questioninstance.repository.QuestionInstanceRepository;
import com.qmate.exception.custom.matchinstance.MatchNotFoundException;
import com.qmate.exception.custom.question.CustomQuestionLockedException;
import com.qmate.exception.custom.question.CustomQuestionNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CustomQuestionService {

  private final CustomQuestionRepository customQuestionRepository;
  private final MatchRepository matchRepository;
  private final QuestionInstanceRepository questionInstanceRepository;

  /**
   * 커스텀 질문 생성
   *
   * @param userId  요청 사용자 ID
   * @param matchId 질문이 속할 Match ID
   * @param request 질문 생성 요청
   * @return 생성된 커스텀 질문 정보
   */
  public CustomQuestionResponse create(Long userId, Long matchId, CustomQuestionTextRequest request) {

    // Match 와 User 권한 확인 후 Match 엔티티 로드
    Match match = matchRepository.findAuthorizedById(matchId, userId)
        .orElseThrow(MatchNotFoundException::new);

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
    CustomQuestion entity = customQuestionRepository.findByIdAndCreatedBy(id, userId)
        .orElseThrow(CustomQuestionNotFoundException::new);

    // 수정 가능한 상태인지 확인 : QuestionInstance 존재 여부로 판단
    if (questionInstanceRepository.existsByCustomQuestion_Id(entity.getId())) {
      throw new CustomQuestionLockedException();
    }

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
  public void delete(Long userId, Long id) {
    CustomQuestion entity = customQuestionRepository.findByIdAndCreatedBy(id, userId)
        .orElseThrow(CustomQuestionNotFoundException::new);
    // 삭제 가능한 상태인지 확인 : QuestionInstance 존재 여부로 판단
    if (questionInstanceRepository.existsByCustomQuestion_Id(entity.getId())) {
      throw new CustomQuestionLockedException();
    }
    customQuestionRepository.delete(entity);
  }

  /**
   * 커스텀 질문 단건 조회
   *
   * @param id 조회할 커스텀 질문 ID
   * @return 커스텀 질문 정보
   */
  public CustomQuestionResponse getOne(Long userId, Long id) {
    CustomQuestion entity = customQuestionRepository.findByIdAndCreatedBy(id, userId)
        .orElseThrow(CustomQuestionNotFoundException::new);

    // QuestionInstance 존재 여부로 판단 (존재시 수정 불가)
    boolean editable = !questionInstanceRepository.existsByCustomQuestion_Id(entity.getId());
    return QuestionMapper.toResponse(entity, editable, entity.getMatch());
  }

  /**
   * 커스텀 질문 목록 조회 (페이징)
   *
   * @param ownerUserId 질문 소유자(생성자) ID
   * @param matchId     매치 ID
   * @param status      질문 상태 필터 (null 가능)
   * @param pageable    페이징 정보
   * @return 필터링된 커스텀 질문 목록 (페이징)
   */
  public Page<CustomQuestionResponse> findPageByOwnerAndStatusFilter(Long ownerUserId, Long matchId, CustomQuestionStatusFilter status,
      Pageable pageable) {
    return customQuestionRepository.findPageByOwnerAndStatusFilter(ownerUserId, matchId, status, pageable);
  }

}
