package com.qmate.common.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HttpStatusCode {

  // 2xx Success
  public static final String OK = "200";
  public static final String CREATED = "201";
  public static final String NO_CONTENT = "204";

  // 4xx Client Error
  public static final String BAD_REQUEST = "400";
  public static final String UNAUTHORIZED = "401";
  public static final String FORBIDDEN = "403";
  public static final String NOT_FOUND = "404";
  public static final String CONFLICT = "409";
  public static final String GONE = "410";
  public static final String PRECONDITION_FAILED = "412";
  public static final String URI_TOO_LONG = "414";               // aka REQUEST_URI_TOO_LONG
  public static final String LOCKED = "423";                     // WebDAV (RFC 4918)
  public static final String TOO_MANY_REQUESTS = "429";          // RFC 6585

  // 5xx Server Error
  public static final String INTERNAL_SERVER_ERROR = "500";

}
