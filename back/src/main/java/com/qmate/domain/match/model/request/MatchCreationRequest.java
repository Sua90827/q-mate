package com.qmate.domain.match.model.request;

import com.qmate.common.constants.match.MatchConstants;
import com.qmate.common.validation.ValidStartDate;
import com.qmate.domain.match.RelationType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ValidStartDate
public class MatchCreationRequest {

  @NotNull(message = MatchConstants.RELATION_TYPE_NOT_NULL)
  private RelationType relationType;

  private String startDate;

}
