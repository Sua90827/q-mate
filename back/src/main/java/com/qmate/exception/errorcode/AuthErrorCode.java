package com.qmate.exception.errorcode;

import com.qmate.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public class AuthErrorCode extends ErrorCode {

  public static final String INVALID_CREDENTIALS_CODE = "AU_001";
  public static final String INVALID_CREDENTIALS_MESSAGE = "이메일 또는 비밀번호가 올바르지 않습니다.";

  public static final String UNSUPPORTED_PRINCIPAL_TYPE_CODE = "AU_002";
  public static final String UNSUPPORTED_PRINCIPAL_TYPE_MESSAGE = "지원되지 않는 인증 Principal 타입입니다.";

  public static final String OAUTH_RESPONSE_PARSE_FAILED_CODE = "AU_003";
  public static final String OAUTH_RESPONSE_PARSE_FAILED_MESSAGE = "외부 인증 응답 파싱에 실패했습니다.";

  public static final String OAUTH_RESPONSE_MISSING_ID_CODE = "AU_004";
  public static final String OAUTH_RESPONSE_MISSING_ID_MESSAGE = "네이버 사용자 ID가 응답에 없습니다.";

  public static ErrorCode invalidCredential() {
    return new AuthErrorCode(HttpStatus.UNAUTHORIZED, INVALID_CREDENTIALS_CODE, INVALID_CREDENTIALS_MESSAGE );
  }

  public static ErrorCode unsupportedPrincipalType() {
    return new AuthErrorCode(HttpStatus.INTERNAL_SERVER_ERROR, UNSUPPORTED_PRINCIPAL_TYPE_CODE, UNSUPPORTED_PRINCIPAL_TYPE_MESSAGE);
  }

  public static ErrorCode oauthResponseParseFailed() {
    return new AuthErrorCode(HttpStatus.BAD_GATEWAY, OAUTH_RESPONSE_PARSE_FAILED_CODE, OAUTH_RESPONSE_PARSE_FAILED_MESSAGE);
  }

  public static ErrorCode oauthResponseMissingId() {
    return new AuthErrorCode(HttpStatus.BAD_GATEWAY, OAUTH_RESPONSE_MISSING_ID_CODE, OAUTH_RESPONSE_MISSING_ID_MESSAGE);
  }

  private AuthErrorCode(HttpStatus httpStatus, String code, String message){
    super(httpStatus, code, message);
  }
}
