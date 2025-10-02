package com.qmate.domain.match.repository;

import com.qmate.domain.match.MatchMember;
import com.qmate.domain.match.MatchStatus;
import com.qmate.domain.match.QMatchMember;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MatchMemberRepositoryImpl implements MatchMemberRepositoryCustom{

  private final JPAQueryFactory queryFactory;

  @Override
  public Optional<MatchMember> findDetachedMatchForUser(Long userId, MatchStatus status){
    QMatchMember matchMember = QMatchMember.matchMember;

    MatchMember result = queryFactory
        .selectFrom(matchMember)
        .where(
            matchMember.user.id.eq(userId),
            matchMember.match.status.eq(status)
        )
        .orderBy(matchMember.match.detachedAt.desc())
        .fetchFirst(); // LIMIT 1과 동일

    return Optional.ofNullable(result);
  }


}
