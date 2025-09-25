package com.qmate.domain.match.repository;

import com.qmate.domain.match.MatchSetting;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MatchSettingRepository extends JpaRepository<MatchSetting, Long> {

}
