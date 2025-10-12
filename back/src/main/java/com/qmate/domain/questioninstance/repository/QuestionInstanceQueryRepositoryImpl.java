package com.qmate.domain.questioninstance.repository;


import static com.qmate.domain.question.entity.QCustomQuestion.customQuestion;
import static com.qmate.domain.question.entity.QQuestion.question;
import static com.qmate.domain.questioninstance.entity.QQuestionInstance.questionInstance;
import static com.qmate.domain.user.QUser.user;

import com.qmate.domain.questioninstance.entity.QuestionInstanceStatus;
import com.qmate.domain.questioninstance.model.response.QIListItem;
import com.qmate.domain.questioninstance.model.response.QQIListItem;
import com.qmate.exception.custom.questioninstance.QIInvalidSortKeyException;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class QuestionInstanceQueryRepositoryImpl implements QuestionInstanceQueryRepository {

  private final JPAQueryFactory queryFactory;

  /**
   * 매치의 최신 알림된 질문 인스턴스 ID 조회
   *
   * @param matchId 매치 ID
   * @return Optional&lt;Long&gt; (없으면 empty)
   */
  @Override
  public Optional<Long> findLatestDeliveredIdByMatch(Long matchId) {
    Long qiId = queryFactory
        .select(questionInstance.id)
        .from(questionInstance)
        .where(
            questionInstance.match.id.eq(matchId),
            questionInstance.deliveredAt.isNotNull()
        )
        .orderBy(
            questionInstance.deliveredAt.desc(),
            questionInstance.id.desc()
        )
        .fetchFirst();
    return Optional.ofNullable(qiId);
  }

  /**
   * 질문 인스턴스 목록 조회 (question, customQuestion 조인)
   *
   * @param matchId     매치 ID (필수)
   * @param requesterId 요청자 ID (필수) - 요청자의 currentMatchId가 matchId와 동일해야 결과 반환
   * @param status      질문 인스턴스 상태 (optional)
   * @param from        deliveredAt 시작 범위 (inclusive, optional)
   * @param to          deliveredAt 종료 범위 (exclusive, optional)
   * @param pageable    페이지 정보
   * @return Page&lt;QIListItem&gt;
   */
  @Override
  public Page<QIListItem> findPageByMatchIdForRequesterWithQuestion(
      Long matchId,
      Long requesterId,
      QuestionInstanceStatus status,    // optional
      LocalDateTime from,               // optional: deliveredAt >= from
      LocalDateTime to,                 // optional: deliveredAt <  to
      Pageable pageable
  ) {
    // 1) content
    List<QIListItem> content = queryFactory
        .select(new QQIListItem(
            questionInstance.id,
            questionInstance.deliveredAt,
            questionInstance.status,
            question.text.coalesce(customQuestion.text),
            questionInstance.completedAt
        ))
        .from(questionInstance)
        .leftJoin(questionInstance.question, question)
        .leftJoin(questionInstance.customQuestion, customQuestion)
        .where(
            questionInstance.match.id.eq(matchId),
            requesterInMatch(requesterId),          // ← 추가: 권한 검증(현재 매치 일치)
            statusFilter(status),
            deliveredFrom(from),
            deliveredTo(to)
        )
        .orderBy(toOrderSpecifiers(pageable.getSort()))
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch();

    // 2) total (카운트는 조인 불필요)
    Long total = queryFactory
        .select(questionInstance.id.count())
        .from(questionInstance)
        .where(
            questionInstance.match.id.eq(matchId),
            requesterInMatch(requesterId),          // ← 추가: content와 동일 조건
            statusFilter(status),
            deliveredFrom(from),
            deliveredTo(to)
        )
        .fetchOne();

    return new PageImpl<>(content, pageable, total == null ? 0 : total);
  }

  // ---- Predicates (nullable → 무시) ----
  private BooleanExpression deliveredFrom(LocalDateTime from) {
    return from == null ? null : questionInstance.deliveredAt.goe(from);
  }

  private BooleanExpression deliveredTo(LocalDateTime to) {
    return to == null ? null : questionInstance.deliveredAt.lt(to);
  }

  /** 상태 필터: null이면 EXPIRED 자동 제외, 지정되면 해당 상태만 */
  private BooleanExpression statusFilter(QuestionInstanceStatus status) {
    if (status == null) {
      return questionInstance.status.ne(QuestionInstanceStatus.EXPIRED);
    }
    return questionInstance.status.eq(status);
  }

  /** 요청자의 currentMatchId가 대상 QI의 match.id와 같은지 검증 */
  private BooleanExpression requesterInMatch(Long requesterId) {
    if (requesterId == null) return Expressions.FALSE.isTrue(); // 항상 false
    return questionInstance.match.id.eq(
        JPAExpressions
            .select(user.currentMatchId)
            .from(user)
            .where(user.id.eq(requesterId))
    );
  }

  /**
   * Spring Data {@link Sort}를 QueryDSL {@link OrderSpecifier OrderSpecifier} 배열로 변환한다.
   *
   * <p>동작 요약
   * <ul>
   *   <li>정렬 정보가 없거나( {@code null} / {@code unsorted} ) → 기본 정렬
   *       {@code deliveredAt DESC} 한 개만 반환한다.</li>
   *   <li>정렬 정보가 있으면 전달 순서대로 순회하며
   *       화이트리스트( {@code QISortKey} )에 존재하는 키만 {@code OrderSpecifier}로 변환·누적한다.</li>
   *   <li>알 수 없는 키가 포함되면 {@code QIInvalidSortKeyException}을 던진다.</li>
   *   <li>모든 항목 처리 후에도 결과가 비어 있으면(안전장치) 기본 정렬을 한 번 더 추가한다.</li>
   * </ul>
   *
   * <p>예시
   * <pre>
   * // ?sort=deliveredAt,desc&amp;sort=status,asc
   * // → orderBy(questionInstance.deliveredAt.desc(), questionInstance.status.asc())
   * </pre>
   *
   * @param sort 컨트롤러에서 전달받은 정렬 정보
   * @return QueryDSL에서 사용할 정렬 조건 배열 (요청 순서 유지)
   * @throws QIInvalidSortKeyException 지원하지 않는 정렬 키가 포함된 경우
   */
  private OrderSpecifier<?>[] toOrderSpecifiers(Sort sort) {
    if (sort == null || sort.isUnsorted()) {
      return new OrderSpecifier<?>[]{questionInstance.deliveredAt.desc()}; // 기본 정렬
    }

    List<OrderSpecifier<?>> specs = new ArrayList<>();

    for (Sort.Order o : sort) {
      // 화이트리스트 키인지 확인
      QISortKey k = QISortKey.BY_KEY.get(o.getProperty());
      if (k == null) {
        throw new QIInvalidSortKeyException();
      }

      // ASC/DESC 여부 반영하여 OrderSpecifier 생성 후 누적
      specs.add(k.toOrder(o.isAscending()));
    }

    // 정렬 설정 없으면 기본값 (위에서 처리 했으나 안전장치 차원에서 한 번 더)
    if (specs.isEmpty()) {
      specs.add(new OrderSpecifier<>(Order.DESC, questionInstance.deliveredAt));
    }
    return specs.toArray(OrderSpecifier[]::new);
  }

  /**
   * 목록 정렬용 화이트리스트 Enum.
   *
   * <p>역할
   * <ul>
   *   <li>클라이언트가 전달한 정렬 키(String)를 엔티티의 타입 세이프한 QueryDSL 경로로 매핑한다.</li>
   *   <li>허용된 필드만 정렬하도록 제한(화이트리스트)하여 오타·미지원 키를 차단한다.</li>
   *   <li>정렬 키 문자열은 Q타입 {@code Path}의 메타데이터에서 직접 가져와
   *       엔티티 필드명 변경 시에도 자동 동기화된다.</li>
   * </ul>
   *
   * <p>구성
   * <ul>
   *   <li>{@code key} : 클라이언트가 넘기는 정렬 키 문자열.
   *       {@code ((Path<?>) path).getMetadata().getName()}로부터 도출된다.</li>
   *   <li>{@code path}: QueryDSL 정렬 대상 경로(예: {@code questionInstance.deliveredAt}).</li>
   *   <li>{@code BY_KEY}: {@code key} → {@code QISortKey} 역조회 맵. 런타임 매핑에 사용한다.</li>
   * </ul>
   *
   * <p>동작
   * <ol>
   *   <li>컨트롤러에서 받은 {@link org.springframework.data.domain.Sort}를 순회하며
   *       {@code BY_KEY.get(order.getProperty())}로 열거형을 조회한다.</li>
   *   <li>일치 항목이 있으면 {@link #toOrder(boolean)}로
   *       {@link com.querydsl.core.types.OrderSpecifier}를 생성하여
   *       {@code JPAQuery.orderBy(...)}에 전달한다.</li>
   *   <li>일치 항목이 없으면 예외를 던진다.</li>
   * </ol>
   *
   * <p>필요 키를 추가할 때는 열거형 상수만 늘리면 된다
   *       (예: {@code QUESTION_TEXT(question.text)}).
   */
  private enum QISortKey {
    DELIVERED_AT(questionInstance.deliveredAt),
    COMPLETED_AT(questionInstance.completedAt),
    STATUS(questionInstance.status);

    final String key;                               // 클라이언트가 넘기는 정렬 키 문자열
    final Expression<? extends Comparable<?>> path; // OrderSpecifier에 쓸 경로

    QISortKey(Expression<? extends Comparable<?>> path) {
      this.path = path;
      this.key = ((Path<?>) path).getMetadata().getName();
    }

    OrderSpecifier<?> toOrder(boolean asc) {
      return new OrderSpecifier<>(asc ? Order.ASC : Order.DESC, path);
    }

    static final Map<String, QISortKey> BY_KEY =
        Arrays.stream(values()).collect(Collectors.toMap(k -> k.key, k -> k));
  }
}
