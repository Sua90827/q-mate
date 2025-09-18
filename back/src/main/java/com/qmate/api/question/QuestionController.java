package com.qmate.api.question;

import com.qmate.domain.question.entity.RelationType;
import com.qmate.domain.question.model.request.QuestionCategoryCreateRequest;
import com.qmate.domain.question.model.request.QuestionCategoryUpdateRequest;
import com.qmate.domain.question.model.request.QuestionCreateRequest;
import com.qmate.domain.question.model.request.QuestionUpdateRequest;
import com.qmate.domain.question.model.response.QuestionCategoryResponse;
import com.qmate.domain.question.model.response.QuestionResponse;
import com.qmate.domain.question.service.QuestionCategoryService;
import com.qmate.domain.question.service.QuestionService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// TODO 관리자 권한 설정 -> SecurityConfig에서 /admin/** 경로에 대해 관리자 권한 설정 필요
@RestController
@RequestMapping("/api/admin/questions")
@RequiredArgsConstructor
public class QuestionController {

  private final QuestionService questionService;

  /**
   * 질문 생성
   */
  @PostMapping
  public ResponseEntity<QuestionResponse> createQuestion(
      @RequestBody @Valid QuestionCreateRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(questionService.createQuestion(request));
  }

  /**
   * 질문 수정
   */
  @PatchMapping("/{id}")
  public ResponseEntity<QuestionResponse> updateQuestion(
      @PathVariable Long id,
      @RequestBody @Valid QuestionUpdateRequest request) {
    return ResponseEntity.ok(questionService.updateQuestion(id, request));
  }

  @GetMapping
  public ResponseEntity<Page<QuestionResponse>> getQuestions(
      @RequestParam(required = false) RelationType relationType,
      @RequestParam(required = false) Long categoryId,
      @RequestParam(name = "active", required = false) Boolean active,
      @PageableDefault(page = 0, size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
      Pageable pageable) {
    Page<QuestionResponse> result = questionService.getQuestions(relationType, categoryId, active, pageable);
    return ResponseEntity.ok(result);
  }
}
