package com.qmate.domain.match.model.request;

import com.qmate.common.validation.ValidStartDate;
import com.qmate.domain.match.RelationType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ValidStartDate
public class MatchCreationRequest {

  @NotNull(message = "관계 유형을 선택해주세요.")
  private RelationType relationType;

  private String startDate;

}
