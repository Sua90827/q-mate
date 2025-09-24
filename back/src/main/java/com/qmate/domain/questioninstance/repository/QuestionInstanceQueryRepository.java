package com.qmate.domain.questioninstance.repository;

import com.qmate.domain.questioninstance.entity.InstanceStatus;
import com.qmate.domain.questioninstance.entity.QuestionInstance;
import com.qmate.domain.questioninstance.model.response.QIListItem;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface QuestionInstanceQueryRepository {

  Optional<Long> findLatestNotifiedIdByMatch(Long matchId);

  Optional<QuestionInstance> findDetailWithQuestionAndMatch(Long qiId);

  Page<QIListItem> findList(
      Long matchId,
      InstanceStatus status,
      LocalDateTime from,
      LocalDateTime to,
      Pageable pageable
  );
}
