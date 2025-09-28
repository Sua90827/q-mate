package com.qmate.domain.question.model.request;

import com.qmate.common.constants.question.QuestionCategoryConstants;
import com.qmate.domain.question.entity.RelationType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuestionCategoryCreateRequest {

  @NotNull(message = QuestionCategoryConstants.NAME_NOT_BLANK_MESSAGE)
  @Size(max = QuestionCategoryConstants.MAX_NAME_LENGTH, message = QuestionCategoryConstants.NAME_SIZE_MESSAGE)
  private String name;

  @NotNull(message = QuestionCategoryConstants.RELATION_TYPE_NOT_BLANK_MESSAGE)
  // @Schema(description = "카테고리의 관계 유형", implementation = RelationType.class)
  private RelationType relationType;
}
