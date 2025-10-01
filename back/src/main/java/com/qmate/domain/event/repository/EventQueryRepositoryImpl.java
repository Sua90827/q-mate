package com.qmate.domain.event.repository;

import com.qmate.domain.user.QUser;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
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

  private final JPAQueryFactory qf;

  @Override
  public List<Event> findCandidates(
      Long matchId,
      Long userId,
      LocalDate from,
      LocalDate to,
      EventRepeatType repeatTypeFilter,
      Boolean anniversaryFilter
  ) {
    QEvent e = QEvent.event;
    QMatchMember mm = QMatchMember.matchMember;
    QUser u = QUser.user;

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

    return qf.selectFrom(e)
        .where(where)
        .orderBy(e.id.asc())
        .fetch();
  }
}
