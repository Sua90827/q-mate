package com.qmate.domain.questionrating.service;

import com.qmate.common.redis.rating.RedisQuestionRatingCounter;
import com.qmate.domain.question.entity.Question;
import com.qmate.domain.question.repository.QuestionRepository;
import com.qmate.domain.questionrating.entity.QuestionRating;
import com.qmate.domain.questionrating.mapper.QuestionRatingMapper;
import com.qmate.domain.questionrating.model.request.QuestionRatingRequest;
import com.qmate.domain.questionrating.model.response.QuestionRatingResponse;
import com.qmate.domain.questionrating.repository.QuestionRatingRepository;
import com.qmate.exception.BusinessGlobalException;
import com.qmate.exception.CommonErrorCode;
import com.qmate.exception.custom.question.DuplicateQuestionRatingException;
import com.qmate.exception.custom.question.QuestionNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QuestionRatingService {

  private final QuestionRatingRepository questionRatingRepository;
  private final QuestionRepository questionRepository;
  private final AuditorAware<Long> auditorAware;
  private final RedisQuestionRatingCounter redisQuestionRatingCounter;

  public QuestionRatingResponse create(Long questionId, QuestionRatingRequest req) {
    Question q = questionRepository.findById(questionId)
        .orElseThrow(QuestionNotFoundException::new);

    Long userId = auditorAware.getCurrentAuditor()
        .orElseThrow(() -> new BusinessGlobalException(CommonErrorCode.unauthorized()));

    if (questionRatingRepository.existsByQuestion_IdAndUserId(questionId, userId)) {
      throw new DuplicateQuestionRatingException();
    }

    QuestionRating entity = QuestionRatingMapper.toEntity(req, q);
    QuestionRating saved = questionRatingRepository.save(entity);
    redisQuestionRatingCounter.addDelta(questionId, req.getIsLike());
    return QuestionRatingMapper.toResponse(saved);
  }
}
