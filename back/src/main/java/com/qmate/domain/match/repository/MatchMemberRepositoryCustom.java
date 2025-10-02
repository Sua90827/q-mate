package com.qmate.domain.match.repository;

import com.qmate.domain.match.MatchMember;
import com.qmate.domain.match.MatchStatus;
import java.util.Optional;

public interface MatchMemberRepositoryCustom {

  Optional<MatchMember> findDetachedMatchForUser(Long userId, MatchStatus status);

}
