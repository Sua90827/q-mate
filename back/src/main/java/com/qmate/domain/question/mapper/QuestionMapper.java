package com.qmate.domain.question.mapper;

import com.qmate.domain.match.Match;
import com.qmate.domain.question.entity.CustomQuestion;
import com.qmate.domain.question.entity.Question;
import com.qmate.domain.question.entity.QuestionCategory;
import com.qmate.domain.question.model.request.CustomQuestionTextRequest;
import com.qmate.domain.question.model.request.QuestionCreateRequest;
import com.qmate.domain.question.model.request.QuestionUpdateRequest;
import com.qmate.domain.question.model.response.CategoryInfo;
import com.qmate.domain.question.model.response.CustomQuestionResponse;
import com.qmate.domain.question.model.response.QuestionResponse;
import com.qmate.domain.question.model.response.SourceType;
import java.time.format.DateTimeFormatter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class QuestionMapper {

  private static final DateTimeFormatter ISO = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

  // CreateRequest → Entity
  public static Question toEntity(QuestionCreateRequest request, QuestionCategory category) {
    return Question.builder()
        .category(category)
        .relationType(request.getRelationType())
        .text(request.getText().trim())
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

  // Entity → Admin Response
  public static QuestionResponse toAdminResponse(Question q, QuestionCategory preloadedCategory) {
    CategoryInfo categoryInfo = toCategoryInfo(
        preloadedCategory != null ? preloadedCategory : q.getCategory()
    );

    return new QuestionResponse(
        q.getId(),
        SourceType.ADMIN,
        q.getRelationType(),
        categoryInfo,
        q.getText(),
        q.isActive(),
        q.getCreatedAt().format(ISO),
        q.getUpdatedAt().format(ISO)
    );
  }

  public static QuestionResponse toAdminResponse(Question q) {
    return toAdminResponse(q, null);
  }

  public static CustomQuestion toEntity(Match match, CustomQuestionTextRequest request) {
    return CustomQuestion.builder()
        .match(match)
        .text(request.getText().trim())
        .build();
  }

  public static CustomQuestionResponse toResponse(CustomQuestion cq, boolean isEditable, Match preloadedMatch) {
    Match match = preloadedMatch != null ? preloadedMatch : cq.getMatch();
    return new CustomQuestionResponse(
        cq.getId(),
        SourceType.CUSTOM,
        match.getRelationType(),
        match.getId(),
        cq.getText(),
        isEditable,
        cq.getCreatedAt(),
        cq.getUpdatedAt()
    );
  }

  public static CustomQuestionResponse toResponse(CustomQuestion cq, boolean isEditable) {
    return toResponse(cq, isEditable, null);
  }

  private static CategoryInfo toCategoryInfo(QuestionCategory c) {
    if (c == null) return null;
    return new CategoryInfo(c.getId(), c.getName());
  }
}
