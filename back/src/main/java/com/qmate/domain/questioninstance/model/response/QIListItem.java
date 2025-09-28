package com.qmate.domain.questioninstance.model.response;

import com.qmate.domain.questioninstance.entity.QuestionInstanceStatus;
import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class QIListItem {

  private Long questionInstanceId;
  private LocalDateTime deliveredAt;
  private QuestionInstanceStatus status;
  private String text;
  private LocalDateTime completedAt;

  @QueryProjection
  public QIListItem(Long questionInstanceId, LocalDateTime deliveredAt, QuestionInstanceStatus status,
      String text, LocalDateTime completedAt) {
    this.questionInstanceId = questionInstanceId;
    this.deliveredAt = deliveredAt;
    this.status = status;
    this.text = text;
    this.completedAt = completedAt;
  }
}
