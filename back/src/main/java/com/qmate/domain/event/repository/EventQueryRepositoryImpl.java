package com.qmate.domain.event.repository;

import com.qmate.domain.event.entity.Event;
import com.qmate.domain.event.entity.EventAlarmOption;
import com.qmate.domain.event.entity.EventRepeatType;
import com.qmate.domain.event.entity.QEvent;
import com.qmate.domain.match.MatchStatus;
import com.qmate.domain.match.QMatch;
import com.qmate.domain.match.QMatchMember;
import com.qmate.domain.notification.entity.NotificationCode;
import com.qmate.domain.user.QUser;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.DateExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
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

}
