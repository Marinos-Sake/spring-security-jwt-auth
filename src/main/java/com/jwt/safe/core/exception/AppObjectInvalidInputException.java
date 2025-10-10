package com.jwt.safe.core.exception;

public class AppObjectInvalidInputException extends AppGenericException {
    private static final String DEFAULT_CODE = "INVALID_INPUT";

    public AppObjectInvalidInputException(String code, String message) {
      super(code + DEFAULT_CODE, message);
    }
}
