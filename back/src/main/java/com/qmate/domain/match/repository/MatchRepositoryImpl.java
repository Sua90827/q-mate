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

  return queryFactory
      .select(match)
      .from(match)
      .join(match.members, matchMember)
      .where(match.status.eq(MatchStatus.ACTIVE))
      .groupBy(match.id)
      .having(matchMember.lastAnsweredAt.max().before(cutoffDate))
      .fetch();
  }

}
