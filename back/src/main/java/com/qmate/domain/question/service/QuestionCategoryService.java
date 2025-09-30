package com.qmate.domain.question.service;

import com.qmate.domain.question.entity.QuestionCategory;
import com.qmate.domain.question.mapper.QuestionCategoryMapper;
import com.qmate.domain.question.model.request.QuestionCategoryCreateRequest;
import com.qmate.domain.question.model.request.QuestionCategoryUpdateRequest;
import com.qmate.domain.question.model.response.QuestionCategoryResponse;
import com.qmate.domain.question.repository.QuestionCategoryRepository;
import com.qmate.exception.custom.question.QuestionCategoryAlreadyExistException;
import com.qmate.exception.custom.question.QuestionCategoryNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class QuestionCategoryService {

  private final QuestionCategoryRepository categoryRepository;

  /**
   * 카테고리 생성
   *
   * @param request 카테고리 생성 요청
   * @return 생성된 카테고리 정보
   */
  public QuestionCategoryResponse createCategory(QuestionCategoryCreateRequest request) {
    if (categoryRepository.existsByName(request.getName())) {
      throw new QuestionCategoryAlreadyExistException();
    }
    QuestionCategory category = QuestionCategoryMapper.toEntity(request);
    QuestionCategory saved = categoryRepository.save(category);
    return QuestionCategoryMapper.toResponse(saved);
  }

  /**
   * 카테고리 수정
   *
   * @param id  수정할 카테고리 ID
   * @param request 카테고리 수정 요청
   * @return 수정된 카테고리 정보
   */
  @Transactional
  public QuestionCategoryResponse updateCategory(Long id, QuestionCategoryUpdateRequest request) {
    QuestionCategory category = categoryRepository.findById(id)
        .orElseThrow(QuestionCategoryNotFoundException::new);
    if (request.getName() != null &&
        !request.getName().equals(category.getName()) &&
        categoryRepository.existsByName(request.getName())) {
      throw new QuestionCategoryAlreadyExistException();
    }

    QuestionCategoryMapper.updateEntity(category, request);
    return QuestionCategoryMapper.toResponse(category);
  }

  /**
   * 모든 카테고리 조회
   *
   * @return 카테고리 목록
   */
  @Transactional(readOnly = true)
  public List<QuestionCategoryResponse> getAllCategories() {
    return categoryRepository.findAll().stream()
        .map(QuestionCategoryMapper::toResponse)
        .toList();
  }
}
