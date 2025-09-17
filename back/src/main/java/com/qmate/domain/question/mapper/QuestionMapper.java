package com.qmate.domain.question.mapper;

import com.qmate.domain.question.entity.Question;
import com.qmate.domain.question.entity.QuestionCategory;
import com.qmate.domain.question.model.request.QuestionCreateRequest;
import com.qmate.domain.question.model.request.QuestionUpdateRequest;
import com.qmate.domain.question.model.response.CategoryInfo;
import com.qmate.domain.question.model.response.QuestionResponse;
import java.time.format.DateTimeFormatter;

public class QuestionMapper {

  // CreateRequest → Entity
  public static Question toEntity(QuestionCreateRequest request, QuestionCategory category) {
    return Question.builder()
        .category(category)
        .relationType(request.getRelationType())
        .text(request.getText())
        .isActive(true)
        .likeCount(0L)
        .dislikeCount(0L)
        .build();
  }

  // UpdateRequest → Entity 반영
  public static void updateEntity(Question question, QuestionUpdateRequest request, QuestionCategory category) {
    if (request.getCategoryId() != null && category != null) {
      question.setCategory(category);
    }
    if (request.getRelationType() != null) {
      question.setRelationType(request.getRelationType());
    }
    if (request.getText() != null) {
      question.setText(request.getText());
    }
    if (request.getActive() != null) {
      question.setActive(request.getActive());
    }
  }

  // Entity → Response (고정 스펙)
  public static QuestionResponse toResponseWithCategory(Question q, QuestionCategory c) {
    return new QuestionResponse(
        q.getId(),
        "ADMIN",
        q.getRelationType().name(),
        new CategoryInfo(c.getId(),
            c.getName()),
        q.getText(),
        q.isActive(),
        q.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    );
  }

  public static QuestionResponse toResponse(Question q) {
    return new QuestionResponse(
        q.getId(),
        "ADMIN",
        q.getRelationType().name(),
        new CategoryInfo(q.getCategory().getId(),
            q.getCategory().getName()),
        q.getText(),
        q.isActive(),
        q.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    );
  }
}
