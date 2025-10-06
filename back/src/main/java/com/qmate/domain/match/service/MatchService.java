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
import com.qmate.domain.match.model.response.DetachedMatchStatusResponse;
import com.qmate.domain.match.model.response.InviteCodeValidationResponse;
import com.qmate.domain.match.model.response.LockStatusResponse;
import com.qmate.domain.match.model.response.MatchCreationResponse;
import com.qmate.domain.match.model.response.MatchInfoResponse;
import com.qmate.domain.match.model.response.MatchJoinResponse;
import com.qmate.domain.match.model.response.MatchMembersResponse;
import com.qmate.domain.match.repository.MatchMemberRepository;
import com.qmate.domain.match.repository.MatchRepository;
import com.qmate.domain.match.repository.MatchSettingRepository;
import com.qmate.domain.pet.service.PetService;
import com.qmate.domain.user.User;
import com.qmate.domain.user.UserRepository;
import com.qmate.exception.BusinessGlobalException;
import com.qmate.exception.CommonErrorCode;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MatchService {

  private final MatchRepository matchRepository;
  private final MatchMemberRepository matchMemberRepository;
  private final UserRepository userRepository;
  private final RedisHelper redisHelper;
  private final MatchSettingRepository matchSettingRepository;
  private final PetService petService;

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
    MatchSetting newMatchSetting = new MatchSetting(newMatch);
    newMatch.setMatchSetting(newMatchSetting);

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

      petService.createPetForMatch(match);

      MatchMember partnerMember = findPartner(matchId, joinerId);
      User partner = partnerMember.getUser();

      joiner.joinMatch(match);
      partner.joinMatch(match);
      redisHelper.deleteInviteCode(inviteCode);

      return MatchJoinResponse.builder()
          .matchId(matchId)
          .message("매칭에 성공적으로 참여했습니다.")
          .partnerNickname(partner.getNickname())
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

  /**
   * 특정 매칭의 연결을 끊습니다.
   * @param matchId 연결을 끊을 매칭의 ID
   * @param userId 요청을 보낸 사용자의 ID(권한 검증용)
   */
  @Transactional
  public void disconnectMatch(Long matchId, Long userId){
    Match match = matchRepository.findWithMembersAndUsersById(matchId)
        .orElseThrow(MatchNotFoundException::new);
    boolean isMember = match.getMembers().stream()
        .anyMatch(matchMember -> matchMember.getUser().getId().equals(userId));
    if (!isMember){
      throw new MatchForbiddenException();
    }
    match.getMembers().forEach(matchMember -> matchMember.getUser().leaveMatch());
    match.disconnect();
  }

  /**
   * 끊어진 매칭 연결 복구를 시도합니다.
   * @return 최종 복구 여부 (true: 완전 복구, false: 내 동의만 완료)
   */
  @Transactional
  public boolean restoreMatch(Long matchId, Long userId){
    Match match = matchRepository.findWithMembersAndUsersById(matchId)
        .orElseThrow(MatchNotFoundException::new);
    boolean isMember = match.getMembers().stream()
        .anyMatch(matchMember -> matchMember.getUser().getId().equals(userId));
    if (!isMember){
      throw new MatchForbiddenException();
    }
    //요청자의 MatchMember를 찾습니다.
    MatchMember requester = match.getMembers().stream()
            .filter(matchMember -> matchMember.getUser().getId().equals(userId))
                .findFirst()
                    .orElseThrow();
    requester.agreeToRestore();

    boolean isFullyRestored = match.attemptToRestore();
    if (isFullyRestored){
      match.getMembers().forEach(matchMember -> matchMember.getUser().joinMatch(match));
    }

    return isFullyRestored;
  }

  //초대 코드의 유효성 검증하고, 코드를 생성한 파트너의 닉네임을 반환합니다.
  @Transactional(readOnly = true)
  public InviteCodeValidationResponse validateInviteCode(String inviteCode){
    Long matchId = redisHelper.getMatchIdByInviteCode(inviteCode)
        .orElseThrow(InviteCodeExpiredException::new);//코드가 없거나 만료됨

    Match match = matchRepository.findWithMembersAndUsersById(matchId)
        .orElseThrow(MatchNotFoundException::new);
    if (match.getMembers().isEmpty()){
      throw new BusinessGlobalException(CommonErrorCode.internalServerError());
      //500번 처리 이유: 사용자가 무언가를 잘못했다(4xx)"가 아닌, 우리가 코드를 잘못 짜서 시스템이 고장 났다(500)"는 것을 의미
    }
    String partnerNickname = match.getMembers().get(0).getUser().getNickname();
    return new InviteCodeValidationResponse(true, partnerNickname);
  }

  //사용자의 초대 코드 입력 잠금 상태와 남은 시간을 조회합니다.
  public LockStatusResponse getLockStatus(Long userId){
    // RedisHelper를 통해 사용자의 잠금 남은 시간을 가져옴.
    return redisHelper.getLockTimeRemaining(userId)
        .map(remainingSeconds -> new LockStatusResponse(true, remainingSeconds)) // 잠겨있다면 (시간이 남아있다면)
        .orElse(new LockStatusResponse(false, 0L)); // 잠겨있지 않다면
  }

  //복구 가능 매칭 조회
  @Transactional(readOnly = true)
  public DetachedMatchStatusResponse getDetachedMatchStatus(Long userId){
    //QueryDSL로 구현된 커스텀 리포지토리 메서드를 호출
    Optional<MatchMember> detachedMemberOpt = matchMemberRepository.findDetachedMatchForUser(userId, MatchStatus.DETACHED_PENDING_DELETE);

    if (detachedMemberOpt.isPresent()){
      // 복구 가능한 매칭이 있다면, 해당 MatchMember에서 Match 객체를 꺼내고,
      // 그 Match의 ID를 가져와 DTO에 담아 반환합니다.
      Long matchId = detachedMemberOpt.get().getMatch().getId();
      return new DetachedMatchStatusResponse(true, matchId);
    }else {
      //복구 가능한 매칭 없으면 , false와 null 반환.
      return new DetachedMatchStatusResponse(false, null);
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
  //자동 연결 끊기 서비스 로직 구현
  @Transactional
  public void disconnectInactiveMatches(){
    LocalDateTime toWeeksAgo = LocalDateTime.now().minusWeeks(2);
    List<Match> inactiveMatches = matchRepository.findInactiveMatches(toWeeksAgo);

    for (Match match : inactiveMatches){
      match.getMembers().forEach(matchMember -> matchMember.getUser().leaveMatch());
      match.disconnect();
    }
  log.info("{}개의 비활성 매칭을 연결 끊기 상태로 전환했습니다.", inactiveMatches.size());
  }

  // 유예기간 지난 상태 델리트로 변경.
  @Transactional
  public void finalizeExpiredMatches(){
    LocalDateTime toWeeksAgo = LocalDateTime.now().minusWeeks(2);
    List<Match> expiredMatches = matchRepository.findMatchesForSoftDelete(toWeeksAgo);

    for (Match match : expiredMatches){
      // user의 current_match_id는 이미 null이므로 추가 작업 필요 없음
    match.markAsDeleted();
    }
    log.info("{}개의 만료된 매칭을 삭제 처리했습니다.", expiredMatches.size());
  }
}
