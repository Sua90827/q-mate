package com.qmate.exception;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
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

  // 컨트롤러에서 타입 미스매치 예외를 처리하는 핸들러
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
