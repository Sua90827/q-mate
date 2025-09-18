package com.qmate.common.validation;

import com.qmate.domain.match.RelationType;
import com.qmate.domain.match.model.request.MatchCreationRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.util.StringUtils;

public class StartDateValidator implements ConstraintValidator<ValidStartDate, MatchCreationRequest> {

  @Override
  public boolean isValid(MatchCreationRequest request, ConstraintValidatorContext context){
    if (request.getRelationType() == RelationType.COUPLE){
      // StringUtils.hasText()는 null, "", " " 모두 false로 처리
      return StringUtils.hasText(request.getStartDate());
    }
    return true; // COUPLE이 아닌 경우는 항상 유효
  }
}
