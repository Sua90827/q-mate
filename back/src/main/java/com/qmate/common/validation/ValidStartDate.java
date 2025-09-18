package com.qmate.common.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = StartDateValidator.class)
public @interface ValidStartDate {
  String message() default "연인(COUPLE) 관계는 기념일(startDate)을 필수로 입력해야 합니다.";
  Class<?> [] groups() default {};
  Class<? extends Payload>[] payload() default {};

}
