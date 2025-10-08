package com.qmate.domain.match.service;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.qmate.common.redis.RedisHelper;
import com.qmate.domain.event.service.EventAnniversaryService;
import com.qmate.domain.match.Match;
import com.qmate.domain.match.MatchMember;
import com.qmate.domain.match.MatchSetting;
import com.qmate.domain.match.MatchStatus;
import com.qmate.domain.match.RelationType;
import com.qmate.domain.match.model.request.MatchJoinRequest;
import com.qmate.domain.match.model.request.MatchUpdateRequest;
import com.qmate.domain.match.model.response.MatchInfoResponse;
import com.qmate.domain.match.model.response.MatchMembersResponse;
import com.qmate.domain.match.repository.MatchMemberRepository;
import com.qmate.domain.match.repository.MatchRepository;
import com.qmate.domain.match.repository.MatchSettingRepository;
import com.qmate.domain.notification.repository.NotificationRepository;
import com.qmate.domain.pet.repository.PetRepository;
import com.qmate.domain.pet.service.PetService;
import com.qmate.domain.questioninstance.service.RandomAdminQuestionService;
import com.qmate.domain.user.User;
import com.qmate.domain.user.UserRepository;
import com.qmate.exception.custom.matchinstance.InviteAttemptLockedException;
import com.qmate.exception.custom.matchinstance.MatchForbiddenException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MatchServiceTest {

  @InjectMocks
  private MatchService sut; // sut: System Under Test (테스트 대상 시스템)

  @Mock
  private MatchRepository matchRepository;
  @Mock
  private MatchMemberRepository matchMemberRepository;
  @Mock
  private UserRepository userRepository;
  @Mock
  private RedisHelper redisHelper;
  @Mock
  private MatchSettingRepository matchSettingRepository;
  @Mock
  private PetRepository petRepository;
  @Mock
  private PetService petService;
  @Mock
  private NotificationRepository notificationRepository;
  @Mock
  private EventAnniversaryService eventAnniversaryService;
  @Mock
  RandomAdminQuestionService randomAdminQuestionService;


  @Test
  @DisplayName("매칭 정보 업데이트 성공")
  void updateMatchInfo_success(){
    // given: 4번 매칭에 3번, 4번 유저가 속해있고, 3번 유저가 정보 업데이트를 요청한 상황
    Long matchId = 4L;
    Long requesterId = 3L;
    var request = new MatchUpdateRequest();
    request.setDailyQuestionHour(22);
    request.setStartDate(LocalDate.of(2023, 1, 1));

    User user3 = User.builder().id(3L).build();
    User user4 = User.builder().id(4L).build();
    Match match = Match.builder().id(matchId).build();
    match.addMember(MatchMember.create(user3, match));
    match.addMember(MatchMember.create(user4, match));

    MatchSetting matchSetting = new MatchSetting(match);

    // findWithMembersAndUsersById가 호출되면 가짜 match 객체를 반환하도록 설정
    given(matchRepository.findWithMembersAndUsersById(matchId)).willReturn(Optional.of(match));
    // findById가 호출되면 가짜 matchSetting 객체를 반환하도록 설정
    given(matchSettingRepository.findById(matchId)).willReturn(Optional.of(matchSetting));

    sut.updateMatchInfo(matchId, requesterId, request);

    assertThat(match.getStartDate()).isEqualTo(LocalDate.of(2023, 1, 1).atStartOfDay());
    assertThat(matchSetting.getDailyQuestionHour()).isEqualTo(22);
  }

  @Test
  @DisplayName("매칭 참여 성공: 새로운 Pet이 생성되고 저장된다")
  void joinMatch_success_createsNewPet() {
    // given: 3번 유저(초대자)와 4번 유저(참여자)가 5번 매칭에 참여하는 상황
    Long matchId = 5L;
    Long inviterId = 3L;
    Long joinerId = 4L;

    User inviter = User.builder().id(inviterId).build();
    User joiner = User.builder().id(joinerId).build();
    Match match = Match.builder().id(matchId).build();
    match.addMember(MatchMember.create(inviter, match));

    var request = new MatchJoinRequest();
    request.setInviteCode("123456");

    // Repository들의 행동을 정의(stubbing)
    given(userRepository.findById(joinerId)).willReturn(Optional.of(joiner));
    given(redisHelper.getMatchIdByInviteCode("123456")).willReturn(Optional.of(matchId));
    given(matchRepository.findById(matchId)).willReturn(Optional.of(match));
    given(matchMemberRepository.findAllByMatch_Id(matchId)).willReturn(List.of(match.getMembers().get(0)));

    // when: 4번 유저가 매칭에 참여하면
    sut.joinMatch(request, joinerId);

    // then: petRepository의 save 메서드가 Pet 클래스 타입의 어떤 객체로든 1번 호출되었는지 검증
    verify(petService).createPetForMatch(any(Match.class));
  }

  @Test
  @DisplayName("매칭 멤버 상세 조회 실패: 멤버가 아닌 사용자의 접근")
  void getMatchMembers_fail_forbidden() {
    // given: 3번 매칭에 3번, 4번 유저가 속해있는데, 상관없는 99번 유저가 조회를 요청한 상황
    Long matchId = 3L;
    Long outsiderId = 99L; // 외부인 ID

    User user3 = User.builder().id(3L).build();
    User user4 = User.builder().id(4L).build();
    Match match = Match.builder().id(matchId).build();
    match.addMember(MatchMember.create(user3, match));
    match.addMember(MatchMember.create(user4, match));

    // "matchRepository에서 3번 매칭을 찾아달라고 하면, 이 가짜 match 객체를 돌려줘"
    given(matchRepository.findWithMembersAndUsersById(matchId)).willReturn(Optional.of(match));

    // expect: getMatchMembers를 호출하면 MatchForbiddenException 예외가 발생해야 함
    assertThatThrownBy(() -> sut.getMatchMembers(matchId, outsiderId))
        .isInstanceOf(MatchForbiddenException.class);
  }

  @Test
  @DisplayName("매칭 멤버 상세 조회 성공")
  void getMatchMembers_success() {
    // given: 1번 매칭에 1번, 2번 유저가 속해있고, 1번 유저가 조회를 요청한 상황
    Long matchId = 1L;
    Long requesterId = 1L;
    User user1 = User.builder().id(1L).nickname("유저1").birthDate(LocalDate.of(2000, 1, 1)).build();
    User user2 = User.builder().id(2L).nickname("유저2").birthDate(LocalDate.of(2001, 2, 2)).build();
    Match match = Match.builder().id(matchId).build();
    match.addMember(MatchMember.create(user1, match));
    match.addMember(MatchMember.create(user2, match));

    given(matchRepository.findWithMembersAndUsersById(matchId)).willReturn(Optional.of(match));

    // when: 서비스 메서드를 실행하면
    MatchMembersResponse response = sut.getMatchMembers(matchId, requesterId);

    // then: 반환된 DTO의 내용이 정확한지 검증
    assertThat(response.getMatchId()).isEqualTo(matchId);
    assertThat(response.getMembers()).hasSize(2);
    assertThat(response.getMembers().get(0).getBirthDate()).isEqualTo(LocalDate.of(2000, 1, 1));
  }


  @Test
  @DisplayName("매칭 정보 조회 성공")
  void getMatchInfo_success(){
    // given: 1번 매칭에 1번, 2번 유저가 속해있고, 1번 유저가 조회를 요청한 상황
    Long matchId = 1L;
    Long requesterId = 1L; // 요청자 ID

    User user1 = User.builder().id(1L).nickname("유저1").build();
    User user2 = User.builder().id(2L).nickname("유저2").build();
    Match match = Match.builder().id(matchId).relationType(RelationType.FRIEND).build();

    // Match 엔티티가 멤버 목록을 가지도록 설정
    match.addMember(MatchMember.create(user1, match));
    match.addMember(MatchMember.create(user2, match));

    // matchRepository.findById(1L)이 호출되면, 위에서 만든 가짜 match 객체를 반환하도록 설정
    given(matchRepository.findWithMembersAndUsersById(matchId)).willReturn(Optional.of(match));

    // when: 서비스 메서드를 실행하면
    MatchInfoResponse response = sut.getMatchInfo(matchId, requesterId);

    // then: 반환된 DTO의 내용이 정확한지 검증
    assertThat(response.getMatchId()).isEqualTo(matchId);
    assertThat(response.getRelationType()).isEqualTo(RelationType.FRIEND);
    assertThat(response.getUsers()).hasSize(2);
    assertThat(response.getUsers().get(0).getNickname()).isEqualTo("유저1");
  }

  @Test
  @DisplayName("매칭 정보 조회 실패: 멤버가 아닌 사용자의 접근")
  void getMatchInfo_fail_forbidden() {
    // given: 1번 매칭에 1번, 2번 유저가 속해있는데, 상관없는 99번 유저가 조회를 요청한 상황
    Long matchId = 1L;
    Long outsiderId = 99L; // 외부인 ID

    User user1 = User.builder().id(1L).build();
    User user2 = User.builder().id(2L).build();
    Match match = Match.builder().id(matchId).build();
    match.addMember(MatchMember.create(user1, match));
    match.addMember(MatchMember.create(user2, match));

    given(matchRepository.findWithMembersAndUsersById(matchId)).willReturn(Optional.of(match));

    // expect: getMatchInfo를 호출하면 MatchForbiddenException 예외가 발생해야 함
    assertThatThrownBy(() -> sut.getMatchInfo(matchId, outsiderId))
        .isInstanceOf(MatchForbiddenException.class);
  }

  @Test
  @DisplayName("매칭 참여 실패: 초대 코드 5회 시도 시 계정이 잠긴다")
  void joinMatch_fail_locksAfter5Attempts() {
    // given: 4번 유저가 잘못된 코드를 입력하는 상황
    Long joinerId = 4L;
    var request = new MatchJoinRequest();
    request.setInviteCode("000000"); // 잘못된 코드
    var joiner = User.builder().id(joinerId).build();

    given(redisHelper.isLocked(joinerId)).willReturn(false); // 잠겨있지 않음
    given(userRepository.findById(joinerId)).willReturn(Optional.of(joiner));
    given(matchMemberRepository.findByUser_IdAndMatch_Status(joinerId, MatchStatus.ACTIVE))
        .willReturn(Optional.empty()); // 다른 매칭에 참여 중이지 않음
    given(redisHelper.getMatchIdByInviteCode("000000")).willReturn(Optional.empty()); // 코드 조회 실패

    // 중요: 시도 횟수 증가 시, '5'를 반환하도록 설정
    given(redisHelper.incrementAttemptCount(joinerId)).willReturn(5L);

    // expect: InviteAttemptLockedException 예외가 발생해야 함
    assertThatThrownBy(() -> sut.joinMatch(request, joinerId))
        .isInstanceOf(InviteAttemptLockedException.class);

    // then: lockUser 메서드가 1번 호출되었는지 검증
    verify(redisHelper).lockUser(joinerId);
  }

  @Test
  @DisplayName("매칭 참여 실패: 이미 잠긴 계정으로 참여 시도")
  void joinMatch_fail_alreadyLocked() {
    // given: 3번 유저가 이미 잠겨있는 상황
    Long joinerId = 3L;
    var request = new MatchJoinRequest();
    request.setInviteCode("123456");

    // 중요: 잠겨있다고 설정
    given(redisHelper.isLocked(joinerId)).willReturn(true);

    // expect: InviteAttemptLockedException 예외가 발생해야 함
    assertThatThrownBy(() -> sut.joinMatch(request, joinerId))
        .isInstanceOf(InviteAttemptLockedException.class);

    // then: 다른 어떤 메서드도 호출되지 않았는지 검증
    verify(userRepository, never()).findById(any());
    verify(redisHelper, never()).incrementAttemptCount(any());
  }

}
