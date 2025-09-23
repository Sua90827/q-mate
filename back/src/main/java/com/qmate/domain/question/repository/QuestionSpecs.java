package com.qmate.domain.question.repository;

import com.qmate.domain.question.entity.Question;
import com.qmate.domain.question.entity.RelationType;
import org.springframework.data.jpa.domain.Specification;

public final class QuestionSpecs {
  private QuestionSpecs() {}

  public static Specification<Question> relationEq(RelationType t) {
    return (root, q, cb) -> (t == null)
        ? cb.conjunction()
        : cb.equal(root.get("relationType"), t);
  }

  public static Specification<Question> categoryIdEq(Long categoryId) {
    return (root, q, cb) -> (categoryId == null)
        ? cb.conjunction()
        : cb.equal(root.get("category").get("id"), categoryId);
  }

  public static Specification<Question> activeEq(Boolean active) {
    return (root, q, cb) -> (active == null)
        ? cb.conjunction()
        : cb.equal(root.get("isActive"), active);
  }
}
