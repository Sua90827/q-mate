package com.qmate.domain.questioninstance.mapper;

import com.qmate.domain.match.Match;
import com.qmate.domain.question.entity.CustomQuestion;
import com.qmate.domain.question.entity.Question;
import com.qmate.domain.question.entity.QuestionCategory;
import com.qmate.domain.questioninstance.entity.Answer;
import com.qmate.domain.questioninstance.entity.QuestionInstance;
import com.qmate.domain.questioninstance.model.response.QIDetailResponse;
import com.qmate.domain.questioninstance.model.response.QIDetailResponse.AnswerView;
import com.qmate.domain.questioninstance.model.response.QIDetailResponse.CategoryInfo;
import com.qmate.domain.questioninstance.model.response.QIDetailResponse.QuestionInfo;
import com.qmate.domain.user.User;
import java.util.List;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class QIDetailMapper {

  /**
   * QI 상세 응답 변환
   *
   * @param qi             QI 엔티티 (question/custom, match 미리 로드 권장)
   * @param match          매치 엔티티
   * @param me             요청자 User
   * @param partner        상대 User
   * @param myAnswer       내 답변 (없으면 null)
   * @param partnerAnswer  상대 답변 (없으면 null)
   * @param myVisible      내 답변 공개 여부 (서비스에서 판정, 보통 true)
   * @param partnerVisible 상대 답변 공개 여부 (서비스에서 판정)
   */
  public static QIDetailResponse toResponse(
      QuestionInstance qi,
      Match match,
      User me,
      User partner,
      Answer myAnswer,
      Answer partnerAnswer,
      boolean myVisible,
      boolean partnerVisible
  ) {
    return QIDetailResponse.builder()
        .questionInstanceId(qi.getId())
        .matchId(match.getId())
        .deliveredAt(qi.getDeliveredAt())
        .status(qi.getStatus())
        .completedAt(qi.getCompletedAt())
        .question(mapQuestionInfo(qi, match))
        .answers(List.of(
            toAnswerView(myAnswer, me, true, myVisible),
            toAnswerView(partnerAnswer, partner, false, partnerVisible)
        ))
        .build();
  }

  private static QuestionInfo mapQuestionInfo(QuestionInstance qi, Match match) {
    Question admin = qi.getQuestion();
    CustomQuestion custom = qi.getCustomQuestion();

    if (admin != null) {
      return QuestionInfo.builder()
          .questionId(admin.getId())
          .sourceType("ADMIN")
          .relationType(admin.getRelationType().name())
          .category(mapCategory(admin.getCategory()))
          .text(admin.getText())
          .build();
    } else {
      return QuestionInfo.builder()
          .questionId(custom.getId())
          .sourceType("CUSTOM")
          .relationType(match.getRelationType().name())
          .category(null)
          .text(custom.getText())
          .build();
    }
  }

  private static CategoryInfo mapCategory(QuestionCategory c) {
    if (c == null) {
      return null;
    }
    return CategoryInfo.builder()
        .id(c.getId())
        .name(c.getName())
        .build();
  }

  private static AnswerView toAnswerView(Answer a, User user, boolean isMine, boolean visible) {
    if (a == null) {
      // 미제출 케이스: user 정보는 존재 (userId/nickname), 본문은 null
      return AnswerView.builder()
          .answerId(null)
          .userId(user != null ? user.getId() : null)
          .nickname(user != null ? user.getNickname() : null)
          .isMine(isMine)
          .visible(false)
          .content(null)
          .submittedAt(null)
          .build();
    }
    return AnswerView.builder()
        .answerId(a.getId())
        .userId(user != null ? user.getId() : null)
        .nickname(user != null ? user.getNickname() : null)
        .isMine(isMine)
        .visible(visible)
        .content(visible ? a.getContent() : null)
        .submittedAt(a.getSubmittedAt())
        .build();
  }

}
