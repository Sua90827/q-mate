package com.qmate.domain.match.repository;

import com.qmate.domain.match.MatchMember;
import com.qmate.domain.match.MatchStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MatchMemberRepository extends JpaRepository<MatchMember, Long> {

  Optional<MatchMember> findByMatch_IdAndUser_Id(Long matchId, Long id);

  List<MatchMember> findAllByMatch_Id(Long matchId);

  Optional<MatchMember> findByUser_IdAndMatch_Status(Long id, MatchStatus status);

}
