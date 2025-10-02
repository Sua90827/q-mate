package com.qmate.domain.questionrating.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryLikeStat {

  private Long categoryId;
  private String categoryName;
  private long likeCount;
}
