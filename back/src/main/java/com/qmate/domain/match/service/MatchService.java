package com.qmate.domain.match.service;

import com.qmate.common.redis.RedisHelper;
import com.qmate.domain.match.Match;
import com.qmate.domain.match.MatchMember;
import com.qmate.domain.match.MatchStatus;
import com.qmate.domain.match.RelationType;
import com.qmate.domain.match.model.request.MatchCreationRequest;
import com.qmate.domain.match.model.request.MatchJoinRequest;
import com.qmate.domain.match.model.response.MatchCreationResponse;
import com.qmate.domain.match.model.response.MatchJoinResponse;
import com.qmate.domain.match.repository.MatchMemberRepository;
import com.qmate.domain.match.repository.MatchRepository;
import com.qmate.domain.user.User;
import com.qmate.domain.user.UserRepository;
import com.qmate.exception.custom.AlreadyInMatchException;
import com.qmate.exception.custom.InvalidStartDateForCoupleException;
import com.qmate.exception.custom.InviteCodeExpiredException;
import com.qmate.exception.custom.MatchNotFoundException;
import com.qmate.exception.custom.PartnerNotFoundException;
import com.qmate.exception.custom.UserNotFoundException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MatchService {

  private final MatchRepository matchRepository;
  private final MatchMemberRepository matchMemberRepository;
  private final UserRepository userRepository;
  private final RedisHelper redisHelper;

  //초대 코드 생성 로직
  @Transactional
  public MatchCreationResponse createMatch(MatchCreationRequest request, Long inviterId) {
    //사용자 정보 조회
    User inviter = findUserById(inviterId);
    //이미 활성화된 매칭 확인
    validateUserNotInActiveMatch(inviterId);

    LocalDateTime startDateTime = parseStartDate(request.getRelationType(), request.getStartDate());

    //매치,매치멤버 정적 팩토리 사용
    Match newMatch = Match.create(request.getRelationType(), startDateTime);
    MatchMember inviterMember = MatchMember.create(inviter, newMatch);

    //엔티티 저장
    matchRepository.save(newMatch);
    matchMemberRepository.save(inviterMember);

    String inviteCode = generateUniqueInviteCode(newMatch.getMatchId());
    return MatchCreationResponse.builder()
        .matchId(newMatch.getMatchId())
        .inviteCode(inviteCode)
        .build();
  }

  //초대 코드를 사용하여 기존 매칭에 참여 로직.
  @Transactional
  public MatchJoinResponse joinMatch(MatchJoinRequest request, Long joinerId) {
    // 사전 조건 검증
    User joiner = findUserById(joinerId);
    validateUserNotInActiveMatch(joinerId);

    // 초대 코드 및 매칭 정보 검증
    String inviteCode = request.getInviteCode();
    Long matchId = getMatchIdByInviteCode(inviteCode);
    Match match = findMatchById(matchId);
    validateMatchIsWaiting(match);

    // 매칭 참여 처리
    MatchMember joinerMember = MatchMember.create(joiner, match);
    matchMemberRepository.save(joinerMember);
    match.setStatus(MatchStatus.ACTIVE);

    // 파트너 정보 조회 (헬퍼 메서드 사용)
    MatchMember partner = findPartner(matchId, joinerId);

    // 사용한 초대 코드 삭제
    redisHelper.deleteInviteCode(inviteCode);

    // 결과 반환
    return MatchJoinResponse.builder()
        .matchId(matchId)
        .message("매칭에 성공적으로 참여했습니다.")
        .partnerNickname(partner.getUser().getNickname())
        .build();
  }


  //가독성을 위한 private 헬퍼 메서드(초대 생성 관련)
  private User findUserById(Long userId) {
    return userRepository.findById(userId)
        .orElseThrow(UserNotFoundException::new);
  }

  private void validateUserNotInActiveMatch(Long userId) {
    matchMemberRepository.findByUser_IdAndMatch_Status(userId, MatchStatus.ACTIVE)
        .ifPresent(matchMember -> {
          throw new AlreadyInMatchException();
        });
  }

  private LocalDateTime parseStartDate(RelationType relationType, String startDateString) {
    if (relationType != RelationType.COUPLE) {
      return null;
    }
    try {
      return LocalDate.parse(startDateString).atStartOfDay();
    } catch (DateTimeParseException | NullPointerException e) {
      // startDateString이 null이거나 형식이 잘못된 경우
      throw new InvalidStartDateForCoupleException();
    }
  }

  private String generateUniqueInviteCode(Long matchId) {
    String inviteCode;
    do {
      inviteCode = redisHelper.generateRandomCode();
    } while (!redisHelper.setInviteCode(inviteCode, matchId));
    return inviteCode;
  }

  //가독성을 위한 private 헬퍼 메서드(매칭 조인 관련)
  private Long getMatchIdByInviteCode(String inviteCode) {
    return redisHelper.getMatchIdByInviteCode(inviteCode)
        .orElseThrow(InviteCodeExpiredException::new);
  }

  private Match findMatchById(Long matchId) {
    return matchRepository.findById(matchId)
        .orElseThrow(MatchNotFoundException::new);
  }

  private void validateMatchIsWaiting(Match match) {
    if (match.getStatus() != MatchStatus.WAITING) {
      // 이 경우는 전용 Exception 보다는 상세 메시지를 담는 것이 더 적합할 수 있습니다.
      throw new AlreadyInMatchException();
    }
  }

  //파트너 조회 헬퍼 메서드
  private MatchMember findPartner(Long matchId, Long joinerId) {
    return matchMemberRepository.findAllByMatch_MatchId(matchId).stream()
        .filter(member -> !member.getUser().getId().equals(joinerId))
        .findFirst()
        .orElseThrow(PartnerNotFoundException::new);
  }
}
