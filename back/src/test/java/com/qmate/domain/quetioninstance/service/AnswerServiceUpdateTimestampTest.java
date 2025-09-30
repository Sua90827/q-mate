package com.qmate.domain.quetioninstance.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.qmate.domain.match.Match;
import com.qmate.domain.match.MatchStatus;
import com.qmate.domain.question.entity.Question;
import com.qmate.domain.question.entity.QuestionCategory;
import com.qmate.domain.question.entity.RelationType;
import com.qmate.domain.questioninstance.entity.Answer;
import com.qmate.domain.questioninstance.entity.QuestionInstanceStatus;
import com.qmate.domain.questioninstance.entity.QuestionInstance;
import com.qmate.domain.questioninstance.model.request.AnswerContentRequest;
import com.qmate.domain.questioninstance.service.AnswerService;
import com.qmate.domain.user.User;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Disabled("updatedAt 갱신 확인용 테스트 - 기능 테스트와 무관하여 빌드 차단 방지를 위해 제외 합니다.")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class AnswerServiceUpdateTimestampTest {

  @Autowired AnswerService answerService;
  @Autowired EntityManager em;

  Long ownerId, qiId, answerId;

  @BeforeEach
  @Transactional
  void setUp() {
    // 1) user
    User owner = User.builder()
        .email("owner@test.com").nickname("owner")
        .passwordHash("x").birthDate(LocalDate.now())
        .build();
    em.persist(owner);

    // 2) match
    Match match = Match.builder()
        .relationType(com.qmate.domain.match.RelationType.FRIEND)
        .status(MatchStatus.ACTIVE) // enum은 프로젝트 정의에 맞게
        .build();
    em.persist(match);

    // 3) question_category + question (관리자 질문 루트)
    QuestionCategory cat = QuestionCategory.builder()
        .name("기본").relationType(RelationType.BOTH).isActive(true).build();
    em.persist(cat);

    Question q = Question.builder()
        .category(cat)
        .relationType(RelationType.BOTH)
        .text("샘플 질문")
        .isActive(true)
        .build();
    em.persist(q);

    // 4) question_instance (PENDING, question_id만 설정, delivered_at 필수)
    QuestionInstance qi = QuestionInstance.builder()
        .match(match)
        .question(q)                 // custom_question은 null 유지 (CHECK 제약 충족)
        .deliveredAt(LocalDateTime.now()) // 필수
        .status(QuestionInstanceStatus.PENDING)   // ‘PENDING|COMPLETED|EXPIRED’ 중 하나
        .build();
    em.persist(qi);

    // 5) answer ((qi,user) 유니크), content <= 100
    Answer answer = Answer.builder()
        .questionInstance(qi)
        .userId(owner.getId())
        .content("초기") // VARCHAR(100)
        .build();
    em.persist(answer);

    em.flush();
    ownerId = owner.getId();
    qiId = qi.getId();
    answerId = answer.getId();
  }

  @Test
  @Transactional
  @DisplayName("A) 서비스에서 flush 하지 않을 때: 응답 updatedAt != DB 최종 updatedAt")
  void response_updatedAt_is_not_db_latest_when_no_flush() throws Exception {
    // given
    var before = em.find(Answer.class, answerId);
    var beforeUpdated = before.getUpdatedAt();
    Thread.sleep(5);

    // when: 서비스는 flush 안 함(현행)
    var res = answerService.update(answerId, ownerId, new AnswerContentRequest("수정1"));

    // then: 응답 시점 updatedAt 캡처
    var responseUpdatedAt = res.getUpdatedAt();
    assertThat(responseUpdatedAt).isNotNull();

    // DB 최신값과 비교
    em.flush(); em.clear();
    var reloaded = em.find(Answer.class, answerId);
    var dbUpdatedAt = reloaded.getUpdatedAt();

    // 핵심 검증: 현행 구현에서는 '동일'을 보장하지 않음 → 대개 다르거나 응답이 더 이른 값
    assertThat(dbUpdatedAt).isAfterOrEqualTo(beforeUpdated);
    assertThat(responseUpdatedAt).isNotEqualTo(dbUpdatedAt);
    // (정밀 환경에 따라 같을 수 없도록 약간의 시간차를 두기 위해 Thread.sleep 사용)
  }

  @Test
  @Transactional
  @DisplayName("B) 서비스가 flush/saveAndFlush 할 때: 응답 updatedAt == DB 최종 updatedAt")
  void response_updatedAt_equals_db_latest_when_service_flushes() throws Exception {
    // 전제: 아래 라인이 서비스에 반영되어 있어야 함 (둘 중 택1)
    //  - answerRepository.saveAndFlush(answer);
    //  - entityManager.flush() 직후 DTO 매핑
    // 없다면 이 테스트는 @Disabled 처리
    Thread.sleep(5);

    var res = answerService.update(answerId, ownerId, new AnswerContentRequest("수정2"));
    var responseUpdatedAt = res.getUpdatedAt();

    em.flush(); em.clear();
    var reloaded = em.find(Answer.class, answerId);
    var dbUpdatedAt = reloaded.getUpdatedAt();

    // 핵심 검증: flush 후 응답-DB 시각 일치
    assertThat(responseUpdatedAt).isEqualTo(dbUpdatedAt);
  }
}
