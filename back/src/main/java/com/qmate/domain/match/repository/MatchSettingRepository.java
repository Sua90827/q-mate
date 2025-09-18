package com.qmate.domain.match.repository;

import com.qmate.domain.match.entity.MatchSetting;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MatchSettingRepository extends JpaRepository<MatchSetting, Long> {

  Optional<MatchSetting> findByMatchId(Long matchId);
}
