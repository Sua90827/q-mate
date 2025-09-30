package com.qmate.config;

import static com.qmate.config.OpenApiConfig.COMMON_ERRORS_MD;

import com.qmate.exception.CommonErrorCode;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(
    info = @Info(
        title = "Q-Mate API",
        description = COMMON_ERRORS_MD
    )
)
public class OpenApiConfig {

  public static final String COMMON_ERRORS_MD =
      "API 문서 https://qmate.notion.site/API-26cc12a8ec408024a84de4639eaf3eed\n\n"
          + "## 공통 오류 규약\n"
          + "### 오류 바디: ErrorResponse\n"
          + "```\n"
          + "{\n"
          + "  \"errorCode\": \"string\",\n"
          + "  \"message\": \"string\",\n"
          + "  \"errors\": [\n"
          + "    {\n"
          + "      \"field\": \"string\",\n"
          + "      \"defaultMessage\": \"string\"\n"
          + "    }\n"
          + "  ]\n"
          + "}\n"
          + "```\n"
          + "### 공통 에러 응답\n"
          + "| HTTP | errorCode | message |\n"
          + "|-----:|-----|---------|\n"
          + "|" + 400 + "|" + CommonErrorCode.INVALID_INPUT_ERROR_CODE + "|" + CommonErrorCode.INVALID_INPUT_MESSAGE
          + "|\n"
          + "|" + 401 + "|" + CommonErrorCode.UNAUTHORIZED_ERROR_CODE + "|" + CommonErrorCode.UNAUTHORIZED_MESSAGE
          + "|\n"
          + "|" + 403 + "|" + CommonErrorCode.FORBIDDEN_ERROR_CODE + "|" + CommonErrorCode.FORBIDDEN_MESSAGE
          + "|\n"
          + "|" + 500 + "|" + CommonErrorCode.INTERNAL_SERVER_ERROR_CODE + "|" + CommonErrorCode.INTERNAL_SERVER_ERROR_MESSAGE
          + "|\n";
}
