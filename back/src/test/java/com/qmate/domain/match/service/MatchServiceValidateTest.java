package com.qmate.domain.match.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import com.qmate.common.redis.RedisHelper;
import com.qmate.domain.match.Match;
import com.qmate.domain.match.MatchMember;
import com.qmate.domain.match.model.response.InviteCodeValidationResponse;
import com.qmate.domain.match.repository.MatchRepository;
import com.qmate.domain.user.User;
import com.qmate.exception.BusinessGlobalException;
import com.qmate.exception.custom.matchinstance.InviteCodeExpiredException;
import java.util.Collections;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MatchServiceValidateTest {

  @InjectMocks
  private MatchService sut;

  @Mock
  private RedisHelper redisHelper;
  @Mock
  private MatchRepository matchRepository;

  @Test
  @DisplayName("초대 코드 유효성 검증 성공")
  void validateInviteCode_success() {
    // given: 유효한 초대 코드가 주어졌고, 해당 매칭에 초대자 1명이 있는 정상적인 상황
    String validCode = "123456";
    Long matchId = 1L;
    User inviter = User.builder().id(10L).nickname("초대자닉네임").build();
    Match match = Match.builder().id(matchId).build();
    match.addMember(MatchMember.create(inviter, match));

    given(redisHelper.getMatchIdByInviteCode(validCode)).willReturn(Optional.of(matchId));
    given(matchRepository.findWithMembersAndUsersById(matchId)).willReturn(Optional.of(match));

    // when: 서비스 메서드를 실행하면
    InviteCodeValidationResponse response = sut.validateInviteCode(validCode);

    // then: 유효성 결과는 true이고, 파트너 닉네임이 정확히 반환되어야 한다
    assertThat(response.isValid()).isTrue();
    assertThat(response.getPartnerNickname()).isEqualTo("초대자닉네임");
  }

  @Test
  @DisplayName("초대 코드 유효성 검증 실패: 코드가 존재하지 않거나 만료된 경우")
  void validateInviteCode_fail_codeNotFound() {
    // given: 존재하지 않는 초대 코드가 주어짐
    String invalidCode = "000000";
    given(redisHelper.getMatchIdByInviteCode(invalidCode)).willReturn(Optional.empty());

    // expect: InviteCodeExpiredException 예외가 발생해야 함
    assertThatThrownBy(() -> sut.validateInviteCode(invalidCode))
        .isInstanceOf(InviteCodeExpiredException.class);
  }

  @Test
  @DisplayName("초대 코드 유효성 검증 실패: 매칭에 멤버가 없는 비정상적인 경우 (500)")
  void validateInviteCode_fail_noMemberInMatch() {
    // given: 코드는 유효하지만, 어찌된 일인지 해당 매칭에 멤버가 한 명도 없는 데이터 꼬인 상황
    String validCode = "123456";
    Long matchId = 1L;
    Match matchWithNoMembers = Match.builder().id(matchId).members(Collections.emptyList()).build();

    given(redisHelper.getMatchIdByInviteCode(validCode)).willReturn(Optional.of(matchId));
    given(matchRepository.findWithMembersAndUsersById(matchId)).willReturn(Optional.of(matchWithNoMembers));

    // expect: BusinessGlobalException (500 서버 에러) 예외가 발생해야 함
    assertThatThrownBy(() -> sut.validateInviteCode(validCode))
        .isInstanceOf(BusinessGlobalException.class);
  }
}