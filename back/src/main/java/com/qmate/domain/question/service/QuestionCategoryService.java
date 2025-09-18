package com.qmate.domain.question.service;

import com.qmate.common.constants.question.QuestionCategoryConstants;
import com.qmate.domain.question.entity.QuestionCategory;
import com.qmate.domain.question.mapper.QuestionCategoryMapper;
import com.qmate.domain.question.model.request.QuestionCategoryCreateRequest;
import com.qmate.domain.question.model.request.QuestionCategoryUpdateRequest;
import com.qmate.domain.question.model.response.QuestionCategoryResponse;
import com.qmate.domain.question.repository.QuestionCategoryRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class QuestionCategoryService {

  private final QuestionCategoryRepository categoryRepository;

  @Transactional
  public QuestionCategoryResponse createCategory(QuestionCategoryCreateRequest request) {
    if (categoryRepository.existsByName(request.getName())) {
      // TODO 커스텀 예외로 변경
      throw new IllegalArgumentException(QuestionCategoryConstants.CATEGORY_ALREADY_EXISTS_MESSAGE);
    }
    QuestionCategory category = QuestionCategoryMapper.toEntity(request);
    QuestionCategory saved = categoryRepository.save(category);
    return QuestionCategoryMapper.toResponse(saved);
  }

  @Transactional
  public QuestionCategoryResponse updateCategory(Long id, QuestionCategoryUpdateRequest request) {
    QuestionCategory category = categoryRepository.findById(id)
        // TODO 커스텀 예외로 변경
        .orElseThrow(() -> new EntityNotFoundException(QuestionCategoryConstants.CATEGORY_NOT_FOUND_MESSAGE));
    if (request.getName() != null && !request.getName().equals(category.getName())) {
      if (categoryRepository.existsByName(request.getName())) {
        // TODO 커스텀 예외로 변경
        throw new IllegalArgumentException(QuestionCategoryConstants.CATEGORY_ALREADY_EXISTS_MESSAGE);
      }
    }
    QuestionCategoryMapper.updateEntity(category, request);
    return QuestionCategoryMapper.toResponse(category);
  }

  @Transactional(readOnly = true)
  public List<QuestionCategoryResponse> getAllCategories() {
    return categoryRepository.findAll().stream()
        .map(QuestionCategoryMapper::toResponse)
        .toList();
  }
}
