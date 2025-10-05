package com.qmate.domain.event.repository;

import com.qmate.domain.event.entity.EventAlarmOption;
import com.qmate.domain.match.MatchStatus;
import com.qmate.domain.match.QMatch;
import com.qmate.domain.notification.entity.NotificationCode;
import com.qmate.domain.user.QUser;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.DateExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.qmate.domain.event.entity.Event;
import com.qmate.domain.event.entity.EventRepeatType;
import com.qmate.domain.event.entity.QEvent;
import com.qmate.domain.match.QMatchMember;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class EventQueryRepositoryImpl implements EventQueryRepository {

  private final JPAQueryFactory query;

  private static final QEvent e = QEvent.event;
  private static final QMatch m = QMatch.match;
  private static final QMatchMember mm = QMatchMember.matchMember;
  private static final QUser u = QUser.user;

  @Override
  public List<Event> findCandidates(
      Long matchId,
      Long userId,
      LocalDate from,
      LocalDate to,
      EventRepeatType repeatTypeFilter,
      Boolean anniversaryFilter
  ) {
    BooleanBuilder where = new BooleanBuilder();

    where.and(e.match.id.eq(matchId));
    // 권한 1) 사용자가 매치의 멤버
    where.and(
        JPAExpressions.selectOne()
            .from(mm)
            .where(
                mm.match.id.eq(matchId),
                mm.user.id.eq(userId)
            )
            .exists()
    );

    // 권한 2) 사용자의 currentMatchId == match.id
    where.and(
        JPAExpressions.selectOne()
            .from(u)
            .where(
                u.id.eq(userId),
                u.currentMatchId.eq(matchId)
            )
            .exists()
    );

    // 날짜 후보 조건
    BooleanExpression noneHit = e.repeatType.eq(EventRepeatType.NONE)
        .and(e.eventAt.between(from, to));
    BooleanExpression repeatHit = e.repeatType.ne(EventRepeatType.NONE)
        .and(e.eventAt.loe(to));
    where.and(noneHit.or(repeatHit));

    // 선택 필터
    if (repeatTypeFilter != null) {
      where.and(e.repeatType.eq(repeatTypeFilter));
    }
    if (anniversaryFilter != null) {
      where.and(e.anniversary.eq(anniversaryFilter));
    }

    return query.selectFrom(e)
        .where(where)
        .orderBy(e.id.asc())
        .fetch();
  }

  @Override
  public List<DueEventRow> findDueEventAlarmRows(LocalDate today) {
    LocalDate d0 = today;
    LocalDate d3 = today.plusDays(3);
    LocalDate d7 = today.plusDays(7);

    BooleanExpression occursOnD0 = occursOn(d0);
    BooleanExpression occursOnD3 = occursOn(d3);
    BooleanExpression occursOnD7 = occursOn(d7);

    BooleanExpression sameDay =
        e.alarmOption.eq(EventAlarmOption.SAME_DAY).and(occursOnD0);
    BooleanExpression threeDays =
        e.alarmOption.eq(EventAlarmOption.THREE_DAYS_BEFORE).and(occursOnD3);
    BooleanExpression weekBefore =
        e.alarmOption.eq(EventAlarmOption.WEEK_BEFORE).and(occursOnD7);

    StringExpression codeExpr = new CaseBuilder()
        .when(sameDay).then(NotificationCode.EVENT_SAME_DAY.name())
        .when(threeDays).then(NotificationCode.EVENT_THREE_DAYS_BEFORE.name())
        .when(weekBefore).then(NotificationCode.EVENT_WEEK_BEFORE.name())
        .otherwise("UNKNOWN");

    Expression<LocalDate> occurDateExpr = new CaseBuilder()
        .when(sameDay).then(Expressions.constant(d0))
        .when(threeDays).then(Expressions.constant(d3))
        .when(weekBefore).then(Expressions.constant(d7))
        .otherwise(Expressions.constant(d0));

    DateExpression<LocalDate> occurDateOrderExpr =
        Expressions.dateTemplate(LocalDate.class, "{0}", occurDateExpr);

    BooleanExpression lowerBound = e.eventAt.goe(d7.minusYears(1));

    return query
        .select(Projections.constructor(DueEventRow.class,
            codeExpr,
            e.id,
            e.match.id,
            e.title,
            occurDateExpr
        ))
        .from(e)
        // 🔹 특정 매치 한정 + ACTIVE 확인
        .join(e.match, m)
        .where(
            lowerBound,
            m.status.eq(MatchStatus.ACTIVE),
            sameDay.or(threeDays).or(weekBefore)
        )
        .orderBy(occurDateOrderExpr.asc(), e.id.asc())
        .fetch();
  }

  /**
   * 전개 없이 target 날짜가 발생일인지 평가하는 규칙
   * - WEEKLY : DATEDIFF % 7 == 0
   * - MONTHLY: '일' 일치 or 말일 보정(시작일이 말일이면 대상도 말일 인정)
   * - YEARLY : 월/일 일치 (2/29 보정은 필요시 추가)
   * - NONE   : eventAt == target
   */
  private BooleanExpression occursOn(LocalDate target) {
    NumberExpression<Integer> diffDays =
        Expressions.numberTemplate(Integer.class, "DATEDIFF({0}, {1})", Expressions.constant(target), e.eventAt);

    BooleanExpression weeklyRule =
        e.repeatType.eq(EventRepeatType.WEEKLY)
            .and(e.eventAt.loe(Expressions.constant(target)))
            .and(Expressions.numberTemplate(Integer.class, "MOD({0}, 7)", diffDays).eq(0));

    BooleanExpression monthlyRule =
        e.repeatType.eq(EventRepeatType.MONTHLY)
            .and(e.eventAt.loe(Expressions.constant(target)))
            .and(Expressions.booleanTemplate(
                "(DAY({0}) = DAY({1})) OR (LAST_DAY({0}) = {0} AND LAST_DAY({1}) = {1})",
                Expressions.constant(target), e.eventAt
            ));

    BooleanExpression yearlyRule =
        e.repeatType.eq(EventRepeatType.YEARLY)
            .and(Expressions.booleanTemplate(
                "MONTH({0}) = MONTH({1}) AND DAY({0}) = DAY({1})",
                Expressions.constant(target), e.eventAt
            ));
    // 윤년 2/29 보정 추가
    yearlyRule = yearlyRule.or(
        e.repeatType.eq(EventRepeatType.YEARLY)
            .and(Expressions.booleanTemplate(
                "(MONTH({1})=2 AND DAY({1})=29) AND (MONTH({0})=2 AND LAST_DAY({0})={0})",
                Expressions.constant(target), e.eventAt
            ))
    );

    BooleanExpression noneRule =
        e.repeatType.eq(EventRepeatType.NONE).and(e.eventAt.eq(Expressions.constant(target)));

    return noneRule.or(weeklyRule).or(monthlyRule).or(yearlyRule);
  }
}
