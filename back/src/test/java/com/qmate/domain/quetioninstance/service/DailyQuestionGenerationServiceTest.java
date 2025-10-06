package com.qmate.domain.quetioninstance.service;

import com.qmate.domain.match.Match;
import com.qmate.domain.match.MatchStatus;
import com.qmate.domain.match.repository.MatchRepository;
import com.qmate.domain.question.entity.CustomQuestion;
import com.qmate.domain.question.entity.Question;
import com.qmate.domain.question.entity.RelationType;
import com.qmate.domain.question.repository.CustomQuestionRepository;
import com.qmate.domain.question.repository.QuestionRepository;
import com.qmate.domain.questioninstance.entity.QuestionInstance;
import com.qmate.domain.questioninstance.entity.QuestionInstanceStatus;
import com.qmate.domain.questioninstance.repository.QuestionInstanceRepository;
import com.qmate.domain.questioninstance.service.DailyQuestionGenerationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.BDDMockito.*;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class DailyQuestionGenerationServiceTest {

  @InjectMocks
  private DailyQuestionGenerationService service;

  @Mock private MatchRepository matchRepository;
  @Mock private QuestionInstanceRepository qiRepository;
  @Mock private CustomQuestionRepository customQuestionRepository;
  @Mock private QuestionRepository questionRepository;

  private static final ZoneId KST = ZoneId.of("Asia/Seoul");

  // 공통 now/target 설정 (예: 2025-01-10 11:50 KST → targetDelivery 12:00)
  private ZonedDateTime fixedNow() {
    return ZonedDateTime.of(2025, 1, 10, 11, 50, 0, 0, KST);
  }

  private Match mockMatch(long id, com.qmate.domain.match.RelationType relationType, LocalDateTime startDate) {
    Match m = mock(Match.class);
    given(m.getId()).willReturn(id);
    if (relationType != null)    lenient().when(m.getRelationType()).thenReturn(relationType);
    if (startDate != null) lenient().when(m.getStartDate()).thenReturn(startDate);
    return m;
  }

  @Test
  @DisplayName("만료/스킵: threshold(전날 12:30) 이전 PENDING은 만료, 이후 PENDING 존재 시 생성 스킵")
  void expireOldPending_and_skip_whenRecentPendingExists() {
    // given
    ZonedDateTime now = fixedNow(); // 11:50
    LocalDateTime targetDelivery = now.plusMinutes(10).withSecond(0).withNano(0).toLocalDateTime(); // 12:00
    LocalDateTime threshold = targetDelivery.minusHours(23).minusMinutes(30); // 전날 12:30

    Match match = mockMatch(1L, null, null);
    given(matchRepository.findAllActiveByDailyHour(12, MatchStatus.ACTIVE))
        .willReturn(List.of(match));

    // pendings: 하나는 threshold 이전(만료대상), 하나는 threshold와 같거나 이후(스킵 유발)
    QuestionInstance oldPending = mock(QuestionInstance.class);
    given(oldPending.getCreatedAt()).willReturn(threshold.minusSeconds(1)); // < threshold
    given(oldPending.getStatus()).willReturn(QuestionInstanceStatus.PENDING);

    QuestionInstance recentPending = mock(QuestionInstance.class);
    given(recentPending.getCreatedAt()).willReturn(threshold.plusMinutes(30)); // >= threshold
    given(recentPending.getStatus()).willReturn(QuestionInstanceStatus.PENDING);

    given(qiRepository.findByMatchIdAndStatus(1L, QuestionInstanceStatus.PENDING))
        .willReturn(List.of(oldPending, recentPending));

    // when
    service.generateForNextSlot(now);

    // then
    // 오래된 건 만료 처리(save 호출), 최근 PENDING 존재로 새 QI 생성은 스킵
    verify(oldPending).setStatus(QuestionInstanceStatus.EXPIRED);
    verify(qiRepository, atLeastOnce()).save(oldPending);
    verify(customQuestionRepository, never()).findFirstUnusedForMatchOrderByIdAsc(anyLong(), any());
    verify(questionRepository, never()).findFirstByCategory_NameAndRelationTypeAndIsActiveTrueOrderByIdAsc(anyString(), any());
    verify(questionRepository, never()).findActiveCoupleByCategoryNameOrderByIdAsc(anyString(), any(), any());
    verify(questionRepository, never()).pickOneRandomUnusedAdminQuestion(anyLong(), any());
  }

  @Test
  @DisplayName("생성: 최근 PENDING 없음 + 커스텀 존재 시 커스텀으로 QI 생성")
  void create_whenNoRecentPending_and_customExists() {
    // given
    ZonedDateTime now = fixedNow();
    Match match = mockMatch(1L, com.qmate.domain.match.RelationType.FRIEND, null);
    given(matchRepository.findAllActiveByDailyHour(12, MatchStatus.ACTIVE))
        .willReturn(List.of(match));

    given(qiRepository.findByMatchIdAndStatus(1L, QuestionInstanceStatus.PENDING))
        .willReturn(List.of()); // 최근 PENDING 없음

    CustomQuestion cq = mock(CustomQuestion.class);
    given(customQuestionRepository.findFirstUnusedForMatchOrderByIdAsc(1L, PageRequest.of(0, 1)))
        .willReturn(List.of(cq));

    ArgumentCaptor<QuestionInstance> captor = ArgumentCaptor.forClass(QuestionInstance.class);

    // when
    service.generateForNextSlot(now);

    // then
    verify(qiRepository).save(captor.capture());
    QuestionInstance saved = captor.getValue();
    assertThat(saved.getCustomQuestion()).isEqualTo(cq);
    assertThat(saved.getQuestion()).isNull();
    assertThat(saved.getStatus()).isEqualTo(QuestionInstanceStatus.PENDING);
  }

  @Test
  @DisplayName("생성: 커플 100일이면 100일 질문으로 QI 생성(상수 ID 없이 파생메서드)")
  void create_coupleHundredthDay_question() {
    // given
    ZonedDateTime now = fixedNow();
    LocalDate hundredDaysAgo = LocalDate.now().minusDays(100); // 테스트 시스템 TZ에 의존 → 운영과 동일 TZ로 실행 가정
    Match match = mockMatch(1L, com.qmate.domain.match.RelationType.COUPLE, hundredDaysAgo.atStartOfDay());

    given(matchRepository.findAllActiveByDailyHour(12, MatchStatus.ACTIVE))
        .willReturn(List.of(match));
    given(qiRepository.findByMatchIdAndStatus(1L, QuestionInstanceStatus.PENDING))
        .willReturn(List.of());

    Question q100 = mock(Question.class);
    given(questionRepository.findFirstByCategory_NameAndRelationTypeAndIsActiveTrueOrderByIdAsc("기념일(100일)", RelationType.COUPLE))
        .willReturn(Optional.of(q100));

    ArgumentCaptor<QuestionInstance> captor = ArgumentCaptor.forClass(QuestionInstance.class);

    // when
    service.generateForNextSlot(now);

    // then
    verify(qiRepository).save(captor.capture());
    QuestionInstance saved = captor.getValue();
    assertThat(saved.getQuestion()).isEqualTo(q100);
    assertThat(saved.getCustomQuestion()).isNull();
  }

  @Test
  @DisplayName("생성: 커플 N주년이면 '기념일(N주년)' 카테고리에서 n번째(id 오름차순) 선택")
  void create_coupleNthAnniversary_question() {
    // given
    ZonedDateTime now = fixedNow();
    // 오늘과 월/일 동일, 정확히 2년 전
    LocalDate twoYearsAgoSameDay = LocalDate.now().minusYears(2);
    Match match = mockMatch(1L, com.qmate.domain.match.RelationType.COUPLE, twoYearsAgoSameDay.atStartOfDay());

    given(matchRepository.findAllActiveByDailyHour(12, MatchStatus.ACTIVE))
        .willReturn(List.of(match));
    given(qiRepository.findByMatchIdAndStatus(1L, QuestionInstanceStatus.PENDING))
        .willReturn(List.of());

    // years = 2 → offset = 1, size = 1
    Question qAnniv = mock(Question.class);
    given(questionRepository.findActiveCoupleByCategoryNameOrderByIdAsc("기념일(N주년)", RelationType.COUPLE, PageRequest.of(1, 1)))
        .willReturn(List.of(qAnniv));

    ArgumentCaptor<QuestionInstance> captor = ArgumentCaptor.forClass(QuestionInstance.class);

    // when
    service.generateForNextSlot(now);

    // then
    verify(qiRepository).save(captor.capture());
    assertThat(captor.getValue().getQuestion()).isEqualTo(qAnniv);
  }

  @Test
  @DisplayName("폴백: 커스텀/기념일/주년 모두 불가 시 미사용 랜덤 질문으로 생성")
  void fallback_to_random_when_noCustom_noAnniversary() {
    // given
    ZonedDateTime now = fixedNow();
    Match match = mockMatch(1L, com.qmate.domain.match.RelationType.FRIEND, null);

    given(matchRepository.findAllActiveByDailyHour(12, MatchStatus.ACTIVE))
        .willReturn(List.of(match));
    given(qiRepository.findByMatchIdAndStatus(1L, QuestionInstanceStatus.PENDING))
        .willReturn(List.of());
    given(customQuestionRepository.findFirstUnusedForMatchOrderByIdAsc(1L, PageRequest.of(0, 1)))
        .willReturn(List.of()); // 커스텀 없음
    // 100일/주년 미해당 → 관련 쿼리 미호출 또는 빈 결과
    given(questionRepository.pickOneRandomUnusedAdminQuestion(1L, PageRequest.of(0, 1)))
        .willReturn(List.of(mock(Question.class)));

    // when
    service.generateForNextSlot(now);

    // then
    verify(questionRepository).pickOneRandomUnusedAdminQuestion(eq(1L), eq(PageRequest.of(0, 1)));
    verify(qiRepository, atLeastOnce()).save(any(QuestionInstance.class));
  }

  @Test
  @DisplayName("우선순위: 커스텀 존재 시 100일/주년/랜덤보다 커스텀이 우선")
  void priority_custom_over_anniversary_and_random() {
    // given
    ZonedDateTime now = fixedNow();
    LocalDate hundredDaysAgo = LocalDate.now().minusDays(100);
    Match match = mockMatch(1L, com.qmate.domain.match.RelationType.COUPLE, hundredDaysAgo.atStartOfDay());

    given(matchRepository.findAllActiveByDailyHour(12, MatchStatus.ACTIVE))
        .willReturn(List.of(match));
    given(qiRepository.findByMatchIdAndStatus(1L, QuestionInstanceStatus.PENDING))
        .willReturn(List.of());

    CustomQuestion cq = mock(CustomQuestion.class);
    given(customQuestionRepository.findFirstUnusedForMatchOrderByIdAsc(1L, PageRequest.of(0, 1)))
        .willReturn(List.of(cq));

    // when
    service.generateForNextSlot(now);

    // then: 커스텀 선택 후 나머지는 타지 않음
    verify(qiRepository).save(any(QuestionInstance.class));
    verify(questionRepository, never()).findFirstByCategory_NameAndRelationTypeAndIsActiveTrueOrderByIdAsc(anyString(), any());
    verify(questionRepository, never()).findActiveCoupleByCategoryNameOrderByIdAsc(anyString(), any(), any());
    verify(questionRepository, never()).pickOneRandomUnusedAdminQuestion(anyLong(), any());
  }
}
