package com.qmate.domain.match.repository;

import com.qmate.domain.match.Match;
import com.qmate.domain.match.MatchStatus;
import com.qmate.domain.match.QMatch;
import com.qmate.domain.match.QMatchMember;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MatchRepositoryImpl implements MatchRepositoryCustom{

  private final JPAQueryFactory queryFactory;

  @Override
  public List<Match> findInactiveMatches(LocalDateTime cutoffDate){
    QMatch match = QMatch.match;
    QMatchMember matchMember = QMatchMember.matchMember;

    //기존 두명 다 14일 이상에서, 한명이라도 14일 이상으로 변경.
  return queryFactory
      .select(match).distinct()
      .from(match)
      .join(match.members, matchMember)
      .where(match.status.eq(MatchStatus.ACTIVE),
          matchMember.lastAnsweredAt.before(cutoffDate))
//      .groupBy(match.id)
//      .having(matchMember.lastAnsweredAt.max().before(cutoffDate))
      .fetch();
  }

  @Override
  public List<Match> findMatchesForSoftDelete(LocalDateTime cutoffDate){
    QMatch match = QMatch.match;

    return queryFactory
        .selectFrom(match)
        .where(
            match.status.eq(MatchStatus.DETACHED_PENDING_DELETE),
            match.detachedAt.before(cutoffDate)
        )
        .fetch();
  }

}
