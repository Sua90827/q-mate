package com.qmate.domain.match.repository;

import com.qmate.domain.match.Match;
import java.time.LocalDateTime;
import java.util.List;

public interface MatchRepositoryCustom {

  List<Match> findInactiveMatches(LocalDateTime cutoffDate);

  List<Match> findMatchesForSoftDelete(LocalDateTime cutoffDate);

}
