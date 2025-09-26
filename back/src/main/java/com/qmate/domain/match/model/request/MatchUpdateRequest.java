package com.qmate.domain.match.model.request;

import com.qmate.common.constants.match.MatchConstants;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MatchUpdateRequest {

  @Min(value = 0, message = MatchConstants.HOUR_MIN_MESSAGE)
  @Max(value = 23, message = MatchConstants.HOUR_MAX_MESSAGE)
  private Integer dailyQuestionHour;

  private LocalDate startDate;

}
