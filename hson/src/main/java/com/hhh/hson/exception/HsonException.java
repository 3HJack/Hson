package com.hhh.hson.exception;

public class HsonException extends RuntimeException {

  public HsonException() {
  }

  public HsonException(String message) {
    super(message);
  }

  public HsonException(String message, Throwable cause) {
    super(message, cause);
  }

  public HsonException(Throwable cause) {
    super(cause);
  }
}
