package com.qmate.api.question;

import com.qmate.domain.question.model.request.QuestionCategoryCreateRequest;
import com.qmate.domain.question.model.request.QuestionCategoryUpdateRequest;
import com.qmate.domain.question.model.response.QuestionCategoryResponse;
import com.qmate.domain.question.service.QuestionCategoryService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// TODO 관리자 권한 설정 -> SecurityConfig에서 /admin/** 경로에 대해 관리자 권한 설정 필요
@RestController
@RequestMapping("/api/admin/question-categories")
@RequiredArgsConstructor
public class QuestionCategoryController {

  private final QuestionCategoryService questionCategoryService;

  /**
   * 카테고리 생성
   */
  @PostMapping
  public ResponseEntity<QuestionCategoryResponse> createCategory(
      @RequestBody @Valid QuestionCategoryCreateRequest request) {
    QuestionCategoryResponse response = questionCategoryService.createCategory(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  /**
   * 카테고리 수정
   */
  @PatchMapping("/{id}")
  public ResponseEntity<QuestionCategoryResponse> updateCategory(
      @PathVariable Long id,
      @RequestBody @Valid QuestionCategoryUpdateRequest request) {
    QuestionCategoryResponse response = questionCategoryService.updateCategory(id, request);
    return ResponseEntity.ok(response);
  }

  /**
   * 카테고리 전체 조회
   */
  @GetMapping
  public ResponseEntity<List<QuestionCategoryResponse>> getAllCategories() {
    List<QuestionCategoryResponse> response = questionCategoryService.getAllCategories();
    return ResponseEntity.ok(response);
  }
}
