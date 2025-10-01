package com.qmate.domain.questionrating.repository;

import com.qmate.domain.questionrating.entity.QuestionRating;
import com.qmate.domain.questionrating.repository.projection.RatingAgg;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface QuestionRatingRepository extends JpaRepository<QuestionRating, Long>, QuestionRatingQueryRepository {
  boolean existsByQuestion_IdAndUserId(Long questionId, Long userId);

  @Query("""
      select qr.question.id as questionId,
             sum(case when qr.isLike = true  then 1 else 0 end) as likeCount,
             sum(case when qr.isLike = false then 1 else 0 end) as dislikeCount
        from QuestionRating qr
       group by qr.question.id
      """)
  List<RatingAgg> aggregateAll();
}