package com.qmate.domain.questioninstance.repository;

import com.qmate.domain.questioninstance.entity.QuestionInstanceStatus;
import com.qmate.domain.questioninstance.model.response.QIListItem;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface QuestionInstanceQueryRepository {

  Optional<Long> findLatestDeliveredIdByMatch(Long matchId);

  Page<QIListItem> findPageByMatchIdForRequesterWithQuestion(
      Long matchId,
      Long requesterId,
      QuestionInstanceStatus status,
      LocalDateTime from,
      LocalDateTime to,
      Pageable pageable
  );
}
