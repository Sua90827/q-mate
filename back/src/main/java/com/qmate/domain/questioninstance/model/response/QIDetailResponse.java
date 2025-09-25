package com.qmate.domain.questioninstance.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.qmate.domain.questioninstance.entity.QuestionInstanceStatus;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QIDetailResponse {

  private Long questionInstanceId;
  private Long matchId;
  private LocalDateTime deliveredAt;
  private QuestionInstanceStatus status;     // PENDING | COMPLETED | EXPIRED
  private LocalDateTime completedAt; // COMPLETED 시각(없으면 null)
  private QuestionInfo question;         // 질문 본문(ADMIN/CUSTOM)
  private List<AnswerView> answers;  // [내 답변, 상대 답변] 순서 권장

  // --- Nested DTOs ---
  @Getter
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  public static class QuestionInfo {

    private Long questionId;          // ADMIN 질문 id 또는 CUSTOM 질문 id
    private String sourceType;        // ADMIN | CUSTOM
    private String relationType;      // COUPLE | FRIEND
    private CategoryInfo category;    // ADMIN일 때만 세팅, CUSTOM이면 null
    private String text;              // 질문 본문
  }

  @Getter
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  public static class CategoryInfo {

    private Long id;
    private String name;
  }

  @Getter
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  public static class AnswerView {

    private Long answerId;                // 미제출이면 null
    private Long userId;
    private String nickname;

    @JsonProperty("isMine")               // api 명세 맞춤
    private boolean isMine;               // 요청자 본인 여부
    private boolean visible;              // 공개 여부(PENDING 시 상대 답변 false)
    private String content;               // visible=false면 null
    private LocalDateTime submittedAt;    // 미제출이면 null
  }

}
