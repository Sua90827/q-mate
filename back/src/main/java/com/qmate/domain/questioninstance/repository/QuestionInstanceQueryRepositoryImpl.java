package com.qmate.domain.questioninstance.repository;


import static com.qmate.domain.question.entity.QQuestion.question;
import static com.qmate.domain.question.entity.QCustomQuestion.customQuestion;
import static com.qmate.domain.question.entity.QQuestionCategory.questionCategory;
import static com.qmate.domain.questioninstance.entity.QQuestionInstance.questionInstance;
import static com.qmate.domain.match.QMatch.match;

import com.qmate.domain.questioninstance.entity.QuestionInstance;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class QuestionInstanceQueryRepositoryImpl implements QuestionInstanceQueryRepository {

  private final JPAQueryFactory queryFactory;

  @Override
  public Optional<QuestionInstance> findDetailWithQuestionAndMatch(Long qiId) {
    return Optional.ofNullable(
        queryFactory
            .selectFrom(questionInstance)
            .leftJoin(questionInstance.question, question).fetchJoin()
            .leftJoin(question.category, questionCategory).fetchJoin()
            .leftJoin(questionInstance.customQuestion, customQuestion).fetchJoin()
            .join(questionInstance.match, match).fetchJoin()
            .where(questionInstance.id.eq(qiId))
            .fetchOne()
    );
  }
}
