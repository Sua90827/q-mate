package com.qmate.domain.match.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.qmate.domain.event.service.EventAnniversaryService;
import com.qmate.domain.match.Match;
import com.qmate.domain.match.MatchMember;
import com.qmate.domain.match.model.request.MatchJoinRequest;
import com.qmate.domain.match.repository.MatchMemberRepository;
import com.qmate.domain.match.repository.MatchRepository;
import com.qmate.domain.notification.repository.NotificationRepository;
import com.qmate.domain.pet.service.PetService; // ◀◀◀ PetService import
import com.qmate.domain.questioninstance.service.RandomAdminQuestionService;
import com.qmate.domain.user.User;
import com.qmate.domain.user.UserRepository;
import com.qmate.common.redis.RedisHelper;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MatchServiceJoinTest {

  @InjectMocks
  private MatchService sut;

  @Mock
  private MatchRepository matchRepository;
  @Mock
  private MatchMemberRepository matchMemberRepository;
  @Mock
  private UserRepository userRepository;
  @Mock
  private RedisHelper redisHelper;
  @Mock
  private PetService petService; // ◀◀◀ PetRepository 대신 PetService를 Mock으로 주입
  @Mock
  private NotificationRepository notificationRepository;
  @Mock
  private EventAnniversaryService eventAnniversaryService;
  @Mock
  RandomAdminQuestionService randomAdminQuestionService;


  @Test
  @DisplayName("매칭 참여 성공: 새로운 Pet이 생성된다")
  void joinMatch_success_createsNewPet() {
    // given
    Long matchId = 5L;
    User inviter = User.builder().id(3L).build();
    User joiner = User.builder().id(4L).build();
    Match match = Match.builder().id(matchId).build();
    match.addMember(MatchMember.create(inviter, match));
    var request = new MatchJoinRequest();
    request.setInviteCode("123456");

    given(userRepository.findById(4L)).willReturn(Optional.of(joiner));
    given(redisHelper.getMatchIdByInviteCode("123456")).willReturn(Optional.of(matchId));
    given(matchRepository.findById(matchId)).willReturn(Optional.of(match));
    given(matchMemberRepository.findAllByMatch_Id(matchId)).willReturn(List.of(match.getMembers().get(0)));

    // when
    sut.joinMatch(request, 4L);

    // then: petRepository.save() 대신, petService.createPetForMatch()가 호출되었는지 검증
    verify(petService).createPetForMatch(any(Match.class));
  }
}

