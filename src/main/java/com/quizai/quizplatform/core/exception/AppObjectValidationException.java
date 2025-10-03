package com.quizai.quizplatform.core.exception;

public class AppObjectValidationException extends AppGenericException {

  private static final String DEFAULT_CODE = "VALIDATION";

  public AppObjectValidationException(String code, String message) {
    super(code + DEFAULT_CODE, message);
  }

}
