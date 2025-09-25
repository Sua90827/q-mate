package com.qmate.domain.match.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.qmate.domain.match.Match;
import com.qmate.domain.match.MatchMember;
import com.qmate.domain.match.MatchSetting;
import com.qmate.domain.match.model.request.MatchUpdateRequest;
import com.qmate.domain.match.repository.MatchMemberRepository;
import com.qmate.domain.match.repository.MatchRepository;
import com.qmate.domain.match.repository.MatchSettingRepository;
import com.qmate.domain.user.User;
import com.qmate.exception.custom.matchinstance.MatchForbiddenException;
import com.qmate.exception.custom.matchinstance.MatchNotFoundException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MatchServiceUpdateTest {

  @InjectMocks
  private MatchService sut;

  @Mock
  private MatchRepository matchRepository;
  @Mock
  private MatchSettingRepository matchSettingRepository;

  private User user1, user2;
  private Match match;

  @BeforeEach
  void setUp() {
    // 모든 테스트에서 공통으로 사용할 가짜 데이터 준비
    user1 = User.builder().id(1L).build();
    user2 = User.builder().id(2L).build();
    match = Match.builder().id(100L).build();
    match.addMember(MatchMember.create(user1, match));
    match.addMember(MatchMember.create(user2, match));
  }

  @Test
  @DisplayName("매칭 정보 업데이트 실패: 매칭이 존재하지 않을 경우(404)")
  void updateMatchInfo_fail_matchNotFound() {
    // given: Repository가 비어있는 Optional을 반환하는 상황
    Long nonExistentMatchId = 404L;
    given(matchRepository.findWithMembersAndUsersById(nonExistentMatchId)).willReturn(Optional.empty());

    // expect: MatchNotFoundException 예외가 발생해야 함
    assertThatThrownBy(() -> sut.updateMatchInfo(nonExistentMatchId, 1L, new MatchUpdateRequest()))
        .isInstanceOf(MatchNotFoundException.class);
  }

  @Test
  @DisplayName("매칭 정보 업데이트 실패: 멤버가 아닌 사용자의 접근(403)")
  void updateMatchInfo_fail_forbidden() {
    // given: 100번 매칭 정보를 상관없는 99번 유저가 수정을 요청한 상황
    Long matchId = 100L;
    Long outsiderId = 99L;

    given(matchRepository.findWithMembersAndUsersById(matchId)).willReturn(Optional.of(match));

    // expect: MatchForbiddenException 예외가 발생해야 함
    assertThatThrownBy(() -> sut.updateMatchInfo(matchId, outsiderId, new MatchUpdateRequest()))
        .isInstanceOf(MatchForbiddenException.class);
  }
}