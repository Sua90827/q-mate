package com.qmate.exception;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  // 비즈니스 로직에서 던지는 커스텀 예외는 여기서 처리
  @ExceptionHandler(BusinessGlobalException.class)
  public ResponseEntity<ErrorResponse> handleBusinessException(BusinessGlobalException ex) {
    ErrorCode errorCode = ex.getErrorCode();
    log.warn("Business Exception: {}", errorCode.getMessage());
    return new ResponseEntity<>(
        new ErrorResponse(errorCode.getCode(), errorCode.getMessage()),
        errorCode.getHttpStatus()
    );
  }

  //유효성 검사 실패 예외를 처리하는 핸들러
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidationException(
      MethodArgumentNotValidException ex) {
    log.warn("유효성 검사 실패: {}", ex.getMessage());

    ErrorCode errorCode = CommonErrorCode.invalidInput();

    // FieldError를 추출하여 상세 에러 목록을 만듭니다.
    List<ErrorResponse.FieldErrorDetail> errors = ex.getBindingResult()
        .getFieldErrors()
        .stream()
        .map(fieldError -> new ErrorResponse.FieldErrorDetail(fieldError.getField(),
            fieldError.getDefaultMessage()))
        .toList();
    // 400 BAD_REQUEST와 함께 상세 에러 목록을 반환
    return new ResponseEntity<>(
        new ErrorResponse(errorCode.getCode(), errorCode.getMessage(), errors),
        errorCode.getHttpStatus()

    );

  }

  @ExceptionHandler(MissingServletRequestParameterException.class)
  public ResponseEntity<ErrorResponse> handleMissingParam(MissingServletRequestParameterException ex) {
    log.warn("필수 파라미터 누락: {}", ex.getMessage());

    ErrorCode errorCode = CommonErrorCode.invalidInput();
    String field = ex.getParameterName();
    String requiredType = ex.getParameterType();
    List<ErrorResponse.FieldErrorDetail> errors = List.of(
        new ErrorResponse.FieldErrorDetail(
            field,
            String.format("'%s' 파라미터가 필요합니다. (타입: %s)", field, requiredType)
        )
    );

    return new ResponseEntity<>(
        new ErrorResponse(errorCode.getCode(), errorCode.getMessage(), errors),
        errorCode.getHttpStatus()
    );
  }

  // 컨트롤러에서 타입 미스매치 예외를 처리하는 핸들러 (파라미터 전용)
  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
    log.warn("파라미터 타입 불일치: {}", ex.getMessage());

    ErrorCode errorCode = CommonErrorCode.invalidInput();
    String field = ex.getName();
    Object rejected = ex.getValue();
    String detailMsg = "잘못된 파라미터 값입니다.";

    // Enum인 경우 허용 가능한 값 힌트 제공
    Class<?> required = ex.getRequiredType();
    if (required != null && required.isEnum()) {
      String allowed = Arrays.stream(required.getEnumConstants())
          .map(Object::toString)
          .collect(Collectors.joining(", "));
      detailMsg = "허용 가능한 값: " + allowed;
    }

    List<ErrorResponse.FieldErrorDetail> errors = List.of(
        new ErrorResponse.FieldErrorDetail(
            field,
            String.format("입력값 '%s'은(는) 유효하지 않습니다. %s", rejected, detailMsg)
        )
    );

    return new ResponseEntity<>(
        new ErrorResponse(errorCode.getCode(), errorCode.getMessage(), errors),
        errorCode.getHttpStatus()
    );
  }

  // 컨트롤러에서 본문(JSON) 파싱/바인딩 실패 예외를 처리하는 핸들러 (RequestBody 전용)
  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ErrorResponse> handleNotReadable(HttpMessageNotReadableException ex) {
    Throwable cause = ex.getMostSpecificCause();
    ErrorCode errorCode = CommonErrorCode.invalidInput();
    List<ErrorResponse.FieldErrorDetail> details = new ArrayList<>();

    // 1) JSON 문법 오류
    if (cause instanceof JsonParseException) {
      details.add(new ErrorResponse.FieldErrorDetail("$", "요청 본문 JSON 형식이 올바르지 않습니다."));
      return ResponseEntity.status(errorCode.getHttpStatus())
          .body(new ErrorResponse(errorCode.getCode(), errorCode.getMessage(), details));
    }

    // 2) 타입 불일치(ENUM/숫자/불리언 등)
    if (cause instanceof InvalidFormatException) {
      InvalidFormatException ife = (InvalidFormatException) cause;

      String field = "$";
      if (ife.getPath() != null && !ife.getPath().isEmpty()) {
        field = ife.getPath().getLast().getFieldName();
        if (field == null) field = "$";
      }

      Object rejectedValue = ife.getValue();
      Class<?> targetType = ife.getTargetType();

      if (targetType != null && targetType.isEnum()) {
        String allowed = Arrays.stream(targetType.getEnumConstants())
            .map(Object::toString)
            .collect(Collectors.joining(", "));
        String msg = "입력값 '" + rejectedValue + "'은(는) 유효한 " + targetType.getSimpleName()
            + " 값이 아닙니다. 허용 가능한 값: " + allowed;
        details.add(new ErrorResponse.FieldErrorDetail(field, msg));
      } else {
        String expected = (targetType == null) ? "올바른 타입" : targetType.getSimpleName();
        String msg = "필드 타입이 올바르지 않습니다. value=" + rejectedValue + ", expected=" + expected;
        details.add(new ErrorResponse.FieldErrorDetail(field, msg));
      }

      return ResponseEntity.status(errorCode.getHttpStatus())
          .body(new ErrorResponse(errorCode.getCode(), errorCode.getMessage(), details));
    }

    // 3) 본문 누락/매칭 불가
    if (cause instanceof MismatchedInputException) {
      MismatchedInputException mie = (MismatchedInputException) cause;

      String field = "$";
      if (mie.getPath() != null && !mie.getPath().isEmpty()) {
        field = mie.getPath().getLast().getFieldName();
        if (field == null) field = "$";
      }

      String msg;
      if ("$".equals(field)) {
        msg = "요청 본문이 필요합니다.";
      } else {
        msg = "필드 '" + field + "'의 값이 올바르지 않습니다.";
      }

      details.add(new ErrorResponse.FieldErrorDetail(field, msg));
      return ResponseEntity.status(errorCode.getHttpStatus())
          .body(new ErrorResponse(errorCode.getCode(), errorCode.getMessage(), details));
    }

    // 4) 그 외 NotReadable → 400 통일
    details.add(new ErrorResponse.FieldErrorDetail("$", "요청 본문을 해석할 수 없습니다."));
    return ResponseEntity.status(errorCode.getHttpStatus())
        .body(new ErrorResponse(errorCode.getCode(), errorCode.getMessage(), details));
  }

  //인증 실패 예외를 처리하는 핸들러(401)
  @ExceptionHandler(AuthenticationException.class)
  public ResponseEntity<ErrorResponse> handleAuthenticationException(AuthenticationException ex) {
    log.warn("인증 실패: {}", ex.getMessage());
    ErrorCode errorCode = CommonErrorCode.unauthorized();

    return new ResponseEntity<>(
        new ErrorResponse(errorCode.getCode(), errorCode.getMessage())
        , errorCode.getHttpStatus()
    );
  }

  // 접근 권한 관련 예외를 처리하는 핸들러
  @ExceptionHandler(AuthorizationDeniedException.class)
  public ResponseEntity<ErrorResponse> handleAuthorizationDeniedException(
      AuthorizationDeniedException ex) {
    log.warn("권한이 없는 접근 시도: {}", ex.getMessage());
    ErrorCode errorCode = CommonErrorCode.forbidden();

    // 403 FORBIDDEN 상태를 반환
    return new ResponseEntity<>(
        new ErrorResponse(errorCode.getCode(), errorCode.getMessage()),
        errorCode.getHttpStatus()
    );
  }

  //자주 발생하는 일반 예외들 처리(400,404)
  @ExceptionHandler({IllegalArgumentException.class, NoSuchElementException.class})
  public ResponseEntity<ErrorResponse> handleArgumentException(Exception ex) {
    log.warn("유효하지 않은 인자 또는 없는 요소: {}", ex.getMessage());
    ErrorCode errorCode = CommonErrorCode.invalidInput();
    return new ResponseEntity<>(
        new ErrorResponse(errorCode.getCode(), ex.getMessage()),
        errorCode.getHttpStatus()
    );
  }

  // 예상치 못한 모든 예외를 처리 500 에러
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex) {
    log.error("알 수 없는 에러 발생", ex);
    ErrorCode errorCode = CommonErrorCode.internalServerError();
    return new ResponseEntity<>(
        new ErrorResponse(errorCode.getCode(), errorCode.getMessage()),
        errorCode.getHttpStatus()
    );
  }

}
