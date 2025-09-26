package com.qmate.domain.match.service;

import com.qmate.common.redis.RedisHelper;
import com.qmate.domain.match.Match;
import com.qmate.domain.match.MatchMember;
import com.qmate.domain.match.MatchSetting;
import com.qmate.domain.match.MatchStatus;
import com.qmate.domain.match.RelationType;
import com.qmate.domain.match.model.request.MatchCreationRequest;
import com.qmate.domain.match.model.request.MatchJoinRequest;
import com.qmate.domain.match.model.request.MatchUpdateRequest;
import com.qmate.domain.match.model.response.MatchCreationResponse;
import com.qmate.domain.match.model.response.MatchInfoResponse;
import com.qmate.domain.match.model.response.MatchJoinResponse;
import com.qmate.domain.match.model.response.MatchMembersResponse;
import com.qmate.domain.match.repository.MatchMemberRepository;
import com.qmate.domain.match.repository.MatchRepository;
import com.qmate.domain.match.repository.MatchSettingRepository;
import com.qmate.domain.user.User;
import com.qmate.domain.user.UserRepository;
import com.qmate.exception.custom.matchinstance.AlreadyInMatchException;
import com.qmate.exception.custom.matchinstance.InvalidStartDateForCoupleException;
import com.qmate.exception.custom.matchinstance.InviteAttemptLockedException;
import com.qmate.exception.custom.matchinstance.InviteCodeExpiredException;
import com.qmate.exception.custom.matchinstance.MatchForbiddenException;
import com.qmate.exception.custom.matchinstance.MatchNotFoundException;
import com.qmate.exception.custom.matchinstance.PartnerNotFoundException;
import com.qmate.exception.custom.matchinstance.SelfMatchNotAllowedException;
import com.qmate.exception.custom.matchinstance.UserNotFoundException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
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
  private final MatchSettingRepository matchSettingRepository;

  //초대 코드 생성 로직
  @Transactional
  public MatchCreationResponse createMatch(MatchCreationRequest request, Long inviterId) {
    //사용자 정보 조회
    User inviter = userRepository.findById(inviterId)
        .orElseThrow(UserNotFoundException::new);

    validateUserNotInActiveMatch(inviterId);
    //요청 데이터 처리 및 엔티티 생성
    LocalDateTime startDateTime = parseStartDate(request.getRelationType(), request.getStartDate());
    Match newMatch = Match.create(request.getRelationType(), startDateTime);
    MatchMember inviterMember = MatchMember.create(inviter, newMatch);

    matchRepository.save(newMatch);
    matchMemberRepository.save(inviterMember);

    String inviteCode = generateUniqueInviteCode(newMatch.getId());
    return MatchCreationResponse.builder()
        .matchId(newMatch.getId())
        .inviteCode(inviteCode)
        .build();
  }

  //초대 코드를 사용하여 기존 매칭에 참여 로직.
  @Transactional
  public MatchJoinResponse joinMatch(MatchJoinRequest request, Long joinerId) {
    if (redisHelper.isLocked(joinerId)) {
      throw new InviteAttemptLockedException();
    }

    User joiner = userRepository.findById(joinerId)
        .orElseThrow(UserNotFoundException::new);
    validateUserNotInActiveMatch(joinerId);

    String inviteCode = request.getInviteCode();
    try {

      Long matchId = redisHelper.getMatchIdByInviteCode(inviteCode)
          .orElseThrow(InviteCodeExpiredException::new);

      Match match = matchRepository.findById(matchId)
          .orElseThrow(MatchNotFoundException::new);

      validateJoinAttempt(match, joinerId);

      MatchMember joinerMember = MatchMember.create(joiner, match);
      matchMemberRepository.save(joinerMember);
      match.setStatus(MatchStatus.ACTIVE);

      MatchMember partner = findPartner(matchId, joinerId);
      redisHelper.deleteInviteCode(inviteCode);

      return MatchJoinResponse.builder()
          .matchId(matchId)
          .message("매칭에 성공적으로 참여했습니다.")
          .partnerNickname(partner.getUser().getNickname())
          .build();
    } catch (InviteCodeExpiredException e) {
      //초대 코드가 틀렸을 때 실행되는 실패 로직
      long attempCount = redisHelper.incrementAttemptCount(joinerId);
      if (attempCount >= 5) {
        redisHelper.lockUser(joinerId);
        throw new InviteAttemptLockedException();//5번 실패 시 발생
      }
      throw e; //5번 미만 실패 시 기존의 '유효하지 않은 코드' 예외 발생
    }
  }

  //특정 매칭의 상세 정보를 조회합니다.
  @Transactional(readOnly = true)
  public MatchInfoResponse getMatchInfo(Long matchId, Long userId) {
    Match match = matchRepository.findWithMembersAndUsersById(matchId)
        .orElseThrow(MatchNotFoundException::new);

    boolean isMember = match.getMembers().stream()
        .anyMatch(matchMember -> matchMember.getUser().getId().equals(userId));

    if (!isMember) {
      throw new MatchForbiddenException();
    }
    return new MatchInfoResponse(match, userId);
  }

  //특정 매칭의 구성원 목록(상세 정보)을 조회합니다.
  @Transactional(readOnly = true)
  public MatchMembersResponse getMatchMembers(Long matchId, Long userId) {
    Match match = matchRepository.findWithMembersAndUsersById(matchId)
        .orElseThrow(MatchNotFoundException::new);
    boolean isMember = match.getMembers().stream()
        .anyMatch(matchMember -> matchMember.getUser().getId().equals(userId));
    if (!isMember) {
      throw new MatchForbiddenException();
    }
    return new MatchMembersResponse(match, userId);
  }

  //매칭 정보를 선택적으로 업데이트합니다.(기념일, 질문시간)
  @Transactional
  public void updateMatchInfo(Long matchId, Long userId, MatchUpdateRequest request) {
    Match match = matchRepository.findWithMembersAndUsersById(matchId)
        .orElseThrow(MatchNotFoundException::new);

    boolean isMember = match.getMembers().stream()
        .anyMatch(matchMember -> matchMember.getUser().getId().equals(userId));
    if (!isMember) {
      throw new MatchForbiddenException();
    }
    if (request.getStartDate() != null) {
      match.updateStartDate(request.getStartDate());
    }
    if (request.getDailyQuestionHour() != null) {
      Optional<MatchSetting> optMatchSetting = matchSettingRepository.findById(matchId);

      if (optMatchSetting.isPresent()) {
        //  이미 존재한다면, 찾은 객체의 값을 업데이트합니다.
        MatchSetting matchSetting = optMatchSetting.get();
        matchSetting.updateDailyQuestionHour(request.getDailyQuestionHour());
      } else {
        //  존재하지 않는다면, 새로운 객체를 만들어서 저장합니다.
        MatchSetting newSetting = new MatchSetting(match);
        newSetting.updateDailyQuestionHour(request.getDailyQuestionHour());
        matchSettingRepository.save(newSetting);
      }
    }
  }

  //가독성을 위한 private 헬퍼 메서드(초대 생성 관련)
  private void validateUserNotInActiveMatch(Long userId) {
    matchMemberRepository.findByUser_IdAndMatch_Status(userId, MatchStatus.ACTIVE)
        .ifPresent(matchMember -> {
          throw new AlreadyInMatchException();
        });
  }

  //커플이 아니라면 입력 필요 없어서 = return null
  private LocalDateTime parseStartDate(RelationType relationType, String startDateString) {
    if (relationType != RelationType.COUPLE) {
      return null;
    }
    try {
      return LocalDate.parse(startDateString).atStartOfDay();
    } catch (DateTimeParseException | NullPointerException e) {
      // 커플인데 startDate null이거나 형식이 잘못된 경우
      throw new InvalidStartDateForCoupleException();
    }
  }

  private String generateUniqueInviteCode(Long matchId) {
    for (int i = 0; i < 10; i++) {

      String inviteCode = redisHelper.generateRandomCode();
      if (redisHelper.setInviteCode(inviteCode, matchId)) {
        return inviteCode;
      }
    }
    throw new RuntimeException("초대 코드 생성에 10회 이상 실패했습니다.");
  }

  //가독성을 위한 private 헬퍼 메서드(매칭 조인 관련)
  private void validateJoinAttempt(Match match, Long joinerId) {
    if (match.getStatus() != MatchStatus.WAITING) {
      throw new AlreadyInMatchException();
    }
    List<MatchMember> members = matchMemberRepository.findAllByMatch_Id(match.getId());
    if (members.size() != 1) {
      throw new AlreadyInMatchException(); // 꽉 찼거나 비정상적인 방
    }
    if (members.get(0).getUser().getId().equals(joinerId)) {
      throw new SelfMatchNotAllowedException();
    }
  }

  private MatchMember findPartner(Long matchId, Long joinerId) {
    return matchMemberRepository.findAllByMatch_Id(matchId).stream()
        .filter(member -> !member.getUser().getId().equals(joinerId))
        .findFirst()
        .orElseThrow(PartnerNotFoundException::new);
  }
}
