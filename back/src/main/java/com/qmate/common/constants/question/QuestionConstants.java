package com.qmate.common.constants.question;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class QuestionConstants {

  // 상수
  public static final int MAX_TEXT_LENGTH = 500;

  // 유효성 검사 메시지
  public static final String CATEGORY_ID_NOT_NULL_MESSAGE = "카테고리 ID는 필수입니다.";
  public static final String RELATION_TYPE_NOT_BLANK_MESSAGE = "대상 관계는 필수입니다.";
  public static final String TEXT_NOT_BLANK_MESSAGE = "질문 내용은 필수입니다.";
  public static final String TEXT_SIZE_MESSAGE = "질문은 최대 " + MAX_TEXT_LENGTH + "자까지 입력할 수 있습니다.";

  // 예외 메시지
  public static final String QUESTION_NOT_FOUND_MESSAGE = "질문을 찾을 수 없습니다.";
  public static final String QUESTION_ALREADY_EXISTS_MESSAGE = "이미 존재하는 질문입니다.";
}
