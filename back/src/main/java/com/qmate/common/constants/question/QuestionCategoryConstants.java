package com.qmate.common.constants.question;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class QuestionCategoryConstants {

  // 상수
  public static final String DEFAULT_CATEGORY_NAME = "기본 카테고리";
  public static final int MAX_NAME_LENGTH = 100;

  // 유효성 검사 메시지
  public static final String NAME_NOT_BLANK_MESSAGE = "카테고리명은 필수입니다.";
  public static final String NAME_SIZE_MESSAGE = "카테고리명은 최대 " + MAX_NAME_LENGTH + "자까지 입력할 수 있습니다.";
  public static final String RELATION_TYPE_NOT_BLANK_MESSAGE = "대상 관계는 필수입니다.";

  // 예외 메시지
  public static final String CATEGORY_NOT_FOUND_MESSAGE = "카테고리를 찾을 수 없습니다.";
  public static final String CATEGORY_ALREADY_EXISTS_MESSAGE = "이미 존재하는 카테고리명입니다.";
}
