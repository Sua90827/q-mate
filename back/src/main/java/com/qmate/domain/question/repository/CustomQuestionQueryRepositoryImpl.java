package com.qmate.domain.question.repository;

import static com.qmate.domain.match.QMatch.match;
import static com.qmate.domain.question.entity.QCustomQuestion.customQuestion;
import static com.qmate.domain.questioninstance.entity.QQuestionInstance.questionInstance;

import com.qmate.common.constants.question.CustomQuestionConstants;
import com.qmate.domain.question.entity.CustomQuestion;
import com.qmate.domain.question.model.request.CustomQuestionStatusFilter;
import com.qmate.domain.question.model.response.CustomQuestionResponse;
import com.qmate.domain.question.model.response.SourceType;
import com.qmate.domain.questioninstance.entity.QuestionInstanceStatus;
import com.qmate.exception.custom.question.CustomQuestionInvalidSortKeyException;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CustomQuestionQueryRepositoryImpl implements CustomQuestionQueryRepository {

  private final JPAQueryFactory query;

  @Override
  public Page<CustomQuestionResponse> findPageByOwnerAndStatusFilter(Long userId, Long matchId, CustomQuestionStatusFilter status,
      Pageable pageable) {
    BooleanBuilder where = new BooleanBuilder()
        .and(customQuestion.createdBy.eq(userId)); // 작성자=나

    if (matchId != null) {
      where.and(customQuestion.match.id.eq(matchId));
    }

    // status 필터
    if (status != null) {
      switch (status) {
        case EDITABLE -> where.and(
            JPAExpressions.selectOne()
                .from(questionInstance)
                .where(questionInstance.customQuestion.eq(customQuestion))
                .notExists()
        );
        case PENDING -> where.and(
            JPAExpressions.selectOne()
                .from(questionInstance)
                .where(
                    questionInstance.customQuestion.eq(customQuestion)
                        .and(questionInstance.status.eq(QuestionInstanceStatus.PENDING))
                ).exists()
        );
        case COMPLETED -> where.and(
            JPAExpressions.selectOne()
                .from(questionInstance)
                .where(
                    questionInstance.customQuestion.eq(customQuestion)
                        .and(questionInstance.status.eq(QuestionInstanceStatus.COMPLETED))
                ).exists()
        );
      }
    }

    // editable 계산식
    BooleanExpression editableExpr = JPAExpressions.selectOne()
        .from(questionInstance)
        .where(questionInstance.customQuestion.eq(customQuestion))
        .notExists();

    // 정렬 변환 (Pageable 반영; 화이트리스트 적용)
    OrderSpecifier<?>[] orderSpecifiers = toOrderSpecifiers(pageable.getSort());
    if (orderSpecifiers.length == 0) {
      orderSpecifiers = new OrderSpecifier<?>[]{customQuestion.createdAt.desc()};
    }

    // 데이터 조회: DTO로 바로 프로젝션 (생성자 파라미터 순서 주의)
    List<CustomQuestionResponse> content = query
        .select(Projections.constructor(
            CustomQuestionResponse.class,
            customQuestion.id,                         // customQuestionId
            Expressions.constant(SourceType.CUSTOM),   // ← 상수 주입
            match.relationType,                        // relationType
            match.id,                                  // matchId
            customQuestion.text,                       // text
            editableExpr,                              // isEditable
            customQuestion.createdAt,                  // createdAt (LocalDateTime)
            customQuestion.updatedAt                   // updatedAt (LocalDateTime)
        ))
        .from(customQuestion)
        .join(customQuestion.match, match)
        .where(where)
        .orderBy(orderSpecifiers)
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch();

    Long total = query
        .select(customQuestion.count())
        .from(customQuestion)
        .where(where)
        .fetchOne();

    return new PageImpl<>(content, pageable, total == null ? 0L : total);
  }

  private OrderSpecifier<?>[] toOrderSpecifiers(Sort sort) {
    PathBuilder<?> cq = new PathBuilder<>(CustomQuestion.class, "customQuestion");

    return sort.stream()
        .map(o -> {
          Order dir = o.isAscending() ? Order.ASC : Order.DESC;
          return switch (o.getProperty()) {
            case CustomQuestionConstants.SORT_KEY_ID -> new OrderSpecifier<>(dir, cq.getNumber(CustomQuestionConstants.SORT_KEY_ID, Long.class));
            case CustomQuestionConstants.SORT_KEY_CREATED_AT ->
                new OrderSpecifier<>(dir, cq.get(CustomQuestionConstants.SORT_KEY_CREATED_AT, LocalDateTime.class));
            case CustomQuestionConstants.SORT_KEY_UPDATED_AT ->
                new OrderSpecifier<>(dir, cq.get(CustomQuestionConstants.SORT_KEY_UPDATED_AT, LocalDateTime.class));
            default -> throw new CustomQuestionInvalidSortKeyException();
          };
        })
        .toArray(OrderSpecifier[]::new);
  }
}
