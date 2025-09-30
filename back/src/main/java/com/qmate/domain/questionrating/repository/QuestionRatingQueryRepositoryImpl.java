package com.qmate.domain.questionrating.repository;

import static com.querydsl.jpa.JPAExpressions.selectOne;

import com.qmate.domain.match.QMatchMember;
import com.qmate.domain.question.entity.QQuestion;
import com.qmate.domain.question.entity.QQuestionCategory;
import com.qmate.domain.questioninstance.entity.QQuestionInstance;
import com.qmate.domain.questionrating.entity.QQuestionRating;
import com.qmate.domain.questionrating.model.response.CategoryLikeStat;
import com.qmate.domain.user.QUser;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class QuestionRatingQueryRepositoryImpl implements QuestionRatingQueryRepository {

  private final JPAQueryFactory query;

  @Override
  public List<CategoryLikeStat> findMonthlyLikesByCategory(
      Long userId, Long matchId, LocalDateTime from, LocalDateTime to) {

    QQuestionCategory qc = QQuestionCategory.questionCategory;
    QQuestion q = QQuestion.question;
    QQuestionInstance qi = QQuestionInstance.questionInstance;
    QQuestionRating qr = QQuestionRating.questionRating;
    QMatchMember mm = QMatchMember.matchMember;
    QUser u = QUser.user;

    // 1) 요청자 권한: User.currentMatchId == matchId
    BooleanExpression requesterIsInThisMatch = selectOne()
        .from(u)
        .where(
            u.id.eq(userId),
            u.currentMatchId.eq(matchId)
        )
        .exists();

    // 2) 기간 내 "우리 매치에 노출된" 관리자 질문 존재 여부 (중복 노출은 존재만 확인)
    BooleanExpression existedInMonth = selectOne()
        .from(qi)
        .where(
            qi.question.eq(q),
            qi.match.id.eq(matchId),
            qi.deliveredAt.between(from, to)
        )
        .exists();

    var countDistinctQr = Expressions.numberTemplate(Long.class, "count(distinct {0})", qr.id);

    return query
        .select(Projections.constructor(
            CategoryLikeStat.class,
            qc.id,
            qc.name,
            countDistinctQr
        ))
        .from(q)
        .join(q.category, qc)
        .join(qr)
        .on(qr.question.eq(q))
        .where(
            requesterIsInThisMatch,
            existedInMonth,
            qr.isLike.isTrue()
        )
        .join(mm)
        .on(
            mm.match.id.eq(matchId),
            mm.user.id.eq(qr.userId)
        )
        .groupBy(qc.id, qc.name)
        .orderBy(countDistinctQr.desc(), qc.name.asc())
        .fetch();
  }
}
