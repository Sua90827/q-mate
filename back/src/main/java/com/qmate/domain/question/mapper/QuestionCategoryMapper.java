package com.qmate.domain.question.mapper;

import com.qmate.domain.question.entity.QuestionCategory;
import com.qmate.domain.question.model.request.QuestionCategoryCreateRequest;
import com.qmate.domain.question.model.request.QuestionCategoryUpdateRequest;
import com.qmate.domain.question.entity.RelationType;
import com.qmate.domain.question.model.response.QuestionCategoryResponse;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class QuestionCategoryMapper {

  // CreateRequest → Entity
  public static QuestionCategory toEntity(QuestionCategoryCreateRequest request) {
    return QuestionCategory.builder()
        .name(request.getName())
        .relationType(request.getRelationType())
        .isActive(true)
        .build();
  }

  // Entity → Response
  public static QuestionCategoryResponse toResponse(QuestionCategory category) {
    return new QuestionCategoryResponse(
        category.getId(),
        category.getName(),
        category.getRelationType(),
        category.isActive()
    );
  }

  // UpdateRequest → Entity 반영
  public static void updateEntity(QuestionCategory category, QuestionCategoryUpdateRequest request) {
    if (request.getName() != null) {
      category.setName(request.getName());
    }
    if (request.getRelationType() != null) {
      category.setRelationType(request.getRelationType());
    }
    if (request.getIsActive() != null) {
      category.setActive(request.getIsActive());
    }
  }
}
