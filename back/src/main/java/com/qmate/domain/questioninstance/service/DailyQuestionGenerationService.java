package com.qmate.domain.questioninstance.service;

import com.qmate.domain.match.MatchStatus;
import com.qmate.domain.match.Match;
import com.qmate.domain.match.repository.MatchRepository;
import com.qmate.domain.question.entity.CustomQuestion;
import com.qmate.domain.question.entity.Question;
import com.qmate.domain.question.entity.RelationType;
import com.qmate.domain.question.repository.CustomQuestionRepository;
import com.qmate.domain.question.repository.QuestionRepository;
import com.qmate.domain.questioninstance.entity.QuestionInstance;
import com.qmate.domain.questioninstance.entity.QuestionInstanceStatus;
import com.qmate.domain.questioninstance.repository.QuestionInstanceRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DailyQuestionGenerationService {

  private final MatchRepository matchRepository;
  private final QuestionInstanceRepository qiRepository;
  private final CustomQuestionRepository customQuestionRepository;
  private final QuestionRepository questionRepository;

  /**
   * 매시 50분에 실행: now+10분(정각) 슬롯을 대상으로 처리.
   */
  @Transactional
  public void generateForNextSlot(ZonedDateTime nowKst) {
    LocalDateTime targetDelivery = nowKst.plusMinutes(10).withSecond(0).withNano(0).toLocalDateTime();
    int targetHour = targetDelivery.getHour();
    // 예: 11:50 실행이면 targetDelivery = 오늘 12:00:00
    LocalDateTime threshold = targetDelivery.minusHours(23).minusMinutes(30); // 전날 12:30

    // Match 엔티티 자체를 미리 모두 가져옴
    List<Match> targetMatches = matchRepository.findAllActiveByDailyHour(targetHour, MatchStatus.ACTIVE);

    for (Match match : targetMatches) {
      Long matchId = match.getId();

      // 매치내 PENDING 상태인 qi 조회
      List<QuestionInstance> pendings = qiRepository.findByMatchIdAndStatus(matchId, QuestionInstanceStatus.PENDING);

      // 만료 처리 (created_at < threshold)
      for (QuestionInstance qi : pendings) {
        LocalDateTime created = qi.getCreatedAt(); // 정책상 created_at만 사용
        if (created.isBefore(threshold)) {
          qi.setStatus(QuestionInstanceStatus.EXPIRED);
          qiRepository.save(qi);
        }
      }

      // 이번 슬롯 중복 생성 스킵 (created_at >= threshold 가 하나라도 남아있으면 스킵)
      boolean hasRecentPending = pendings.stream()
          .anyMatch(qi -> qi.getStatus() == QuestionInstanceStatus.PENDING
              && !qi.getCreatedAt().isBefore(threshold)); // created_at >= threshold
      if (hasRecentPending) {
        continue;
      }

      // 커스텀 질문 우선 (미사용 + id 최소)
      List<CustomQuestion> customList = customQuestionRepository.findFirstUnusedForMatchOrderByIdAsc(matchId, PageRequest.of(0,1));
      if (!customList.isEmpty()) {
        CustomQuestion cq = customList.getFirst();
        QuestionInstance qi = QuestionInstance.builder()
            .match(match)
            .customQuestion(cq)
            .status(QuestionInstanceStatus.PENDING)
            .build();
        qiRepository.save(qi);
        continue;
      }

      // 기념일(100일) 또는 N주년
      // 100일: 카테고리명만으로 1개 가져와서 QI 생성
      if (isCouple(match) && isHundredthDay(match)) {
        questionRepository.findFirstByCategory_NameAndRelationTypeAndIsActiveTrueOrderByIdAsc("기념일(100일)", RelationType.COUPLE)
            .ifPresent(q -> qiRepository.save(QuestionInstance.builder()
                .match(match)
                .question(q)
                .status(QuestionInstanceStatus.PENDING) // delivered_at은 실제 발송 시 세팅
                .build()));
        // 100일이면 여기서 끝
        continue;
      }
      // N주년: 몇 주년인지 계산 후, 카테고리 "기념일(N주년)"에서 id 오름차순 기준 n번째를 선택
      int years = getAnniversaryYears(match); // 없으면 0 1주년 이상이면 1,2,3...
      if (isCouple(match) && years > 0) {
        PageRequest page = PageRequest.of(Math.max(0, years - 1), 1);
        List<Question> list = questionRepository.findActiveCoupleByCategoryNameOrderByIdAsc(
            "기념일(N주년)", RelationType.COUPLE, page);
        if (!list.isEmpty()) {
          Question q = list.getFirst();
          qiRepository.save(QuestionInstance.builder()
              .match(match)
              .question(q)
              .status(QuestionInstanceStatus.PENDING)
              .build());
          continue;
        }
      }

      // 4) 일반 질문 랜덤 1개 (미사용)
      questionRepository.pickOneRandomUnusedAdminQuestion(matchId, PageRequest.of(0,1))
          .stream()
          .findFirst()
          .ifPresent(q -> qiRepository.save(QuestionInstance.builder()
              .match(match)
              .question(q)
              .status(QuestionInstanceStatus.PENDING)
              .build()));
    }
  }

  private boolean isCouple(Match match) {
    return match.getRelationType() == com.qmate.domain.match.RelationType.COUPLE;
  }

  private boolean isHundredthDay(Match match) {
    if (match.getStartDate() == null) return false;
    LocalDate s = match.getStartDate().toLocalDate();
    return s.plusDays(100).isEqual(LocalDate.now());
  }

  private int getAnniversaryYears(Match match) {
    if (match.getStartDate() == null) return 0;
    LocalDate s = match.getStartDate().toLocalDate();
    LocalDate today = LocalDate.now();
    if (today.getMonthValue() == s.getMonthValue() && today.getDayOfMonth() == s.getDayOfMonth()) {
      int years = today.getYear() - s.getYear();
      return years >= 1 ? years : 0; // 1주년 이상만 인정
    }
    return 0;
  }
}
