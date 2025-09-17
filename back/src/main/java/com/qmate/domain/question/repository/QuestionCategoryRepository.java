package com.qmate.domain.question.repository;

import com.qmate.domain.question.entity.QuestionCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionCategoryRepository extends JpaRepository<QuestionCategory, Long> {

  boolean existsByName(String name); // 중복 체크용
}