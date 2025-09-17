package com.qmate.domain.question.service;

import com.qmate.common.constants.question.QuestionCategoryConstants;
import com.qmate.common.constants.question.QuestionConstants;
import com.qmate.domain.question.entity.Question;
import com.qmate.domain.question.entity.QuestionCategory;
import com.qmate.domain.question.entity.RelationType;
import com.qmate.domain.question.mapper.QuestionMapper;
import com.qmate.domain.question.model.request.QuestionCreateRequest;
import com.qmate.domain.question.model.request.QuestionUpdateRequest;
import com.qmate.domain.question.model.response.QuestionResponse;
import com.qmate.domain.question.repository.QuestionCategoryRepository;
import com.qmate.domain.question.repository.QuestionRepository;
import com.qmate.domain.question.repository.QuestionSpecs;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class QuestionService {

  private final QuestionRepository questionRepository;
  private final QuestionCategoryRepository categoryRepository;

  /**
   * 질문 생성
   *
   * @param request 질문 생성 요청
   * @return 생성된 질문 정보
   */
  @Transactional
  public QuestionResponse createQuestion(QuestionCreateRequest request) {
    // 카테고리 존재 여부 확인
    QuestionCategory category = loadCategoryOrThrow(request.getCategoryId());

    // 엔티티 변환 및 저장
    Question question = QuestionMapper.toEntity(request, category);
    Question saved = questionRepository.save(question);

    // Response 변환
    return QuestionMapper.toResponseWithCategory(saved, category);
  }

  /**
   * 질문 수정
   *
   * @param id      수정할 질문 ID
   * @param request 질문 수정 요청
   * @return 수정된 질문 정보
   */
  @Transactional
  public QuestionResponse updateQuestion(Long id, QuestionUpdateRequest request) {
    // 질문 존재 여부 확인
    Question question = loadQuestionOrThrow(id);

    // 카테고리 변경 시 체크
    QuestionCategory category = null;
    if (request.getCategoryId() != null) {
      category = loadCategoryOrThrow(request.getCategoryId());
    }

    // 엔티티 수정 반영
    QuestionMapper.updateEntity(question, request, category);

    return QuestionMapper.toResponseWithCategory(question, category != null ? category : question.getCategory());
  }

  @Transactional(readOnly = true)
  public Page<QuestionResponse> getQuestions(RelationType relationType, Long categoryId, Boolean active,
      Pageable pageable) {
    Specification<Question> spec = QuestionSpecs.relationEq(relationType)
        .and(QuestionSpecs.categoryIdEq(categoryId))
        .and(QuestionSpecs.activeEq(active));

    Page<Question> page = questionRepository.findAll(spec, pageable);
    return page.map(QuestionMapper::toResponse);
  }

  // 존재하지 않는 질문 조회 시 예외 발생
  private Question loadQuestionOrThrow(Long id) {
    return questionRepository.findById(id)
        // TODO 커스텀 예외로 변경
        .orElseThrow(() -> new EntityNotFoundException(QuestionConstants.QUESTION_NOT_FOUND_MESSAGE));
  }

  // 존재하지 않는 카테고리 조회 시 예외 발생
  private QuestionCategory loadCategoryOrThrow(Long categoryId) {
    return categoryRepository.findById(categoryId)
        // TODO 커스텀 예외로 변경
        .orElseThrow(() -> new EntityNotFoundException(QuestionCategoryConstants.CATEGORY_NOT_FOUND_MESSAGE));
  }

}
