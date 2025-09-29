package com.qmate.domain.question.repository;

import com.qmate.domain.question.model.request.CustomQuestionStatusFilter;
import com.qmate.domain.question.model.response.CustomQuestionResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomQuestionQueryRepository {

  Page<CustomQuestionResponse> findPageByOwnerAndStatusFilter(Long userId, Long matchId, CustomQuestionStatusFilter status, Pageable pageable);
}
