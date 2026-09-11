package com.nst.myvehiclehub.exception;

import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(InvalidTokenException.class)
  public ResponseEntity<Map<String, String>> handleInvalidTokenException(
      final InvalidTokenException e) {
    Map<String, String> error = new HashMap<>();
    error.put("error", "Unauthorized");
    error.put("message", e.getMessage());
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
  }

  @ExceptionHandler(ExpiredRefreshTokenException.class)
  public ResponseEntity<Map<String, String>> handleExpiredRefreshTokenException(
      final ExpiredRefreshTokenException e) {
    Map<String, String> error = new HashMap<>();
    error.put("error", "Unauthorized");
    error.put("message", e.getMessage());
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
  }

  @ExceptionHandler(RefreshTokenNotFoundException.class)
  public ResponseEntity<Map<String, String>> handleRefreshTokenNotFoundException(
      final RefreshTokenNotFoundException e) {
    Map<String, String> error = new HashMap<>();
    error.put("error", "Unauthorized");
    error.put("message", e.getMessage());
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<Map<String, String>> handleHttpMessageNotReadable(
      HttpMessageNotReadableException e) {
    Map<String, String> error = new HashMap<>();
    error.put("error", "Bad Request");
    error.put("message", "Invalid JSON format or missing required fields");
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, String>> handleValidationException(
      MethodArgumentNotValidException e) {
    Map<String, String> error = new HashMap<>();
    error.put("error", "Bad Request");
    String message =
        e.getBindingResult().getFieldError() != null
            ? e.getBindingResult().getFieldError().getDefaultMessage()
            : "Validation failed";
    error.put("message", "Validation failed: " + message);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
  }

  @ExceptionHandler(ResponseStatusException.class)
  public ResponseEntity<Map<String, String>> handleResponseStatusException(
      ResponseStatusException e) {
    Map<String, String> error = new HashMap<>();
    error.put("error", e.getStatusCode().toString());
    error.put("message", e.getReason() != null ? e.getReason() : "An error occurred");
    return ResponseEntity.status(e.getStatusCode()).body(error);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Map<String, String>> handleGenericException(Exception e) {
    Map<String, String> error = new HashMap<>();
    error.put("error", "Internal Server Error");
    error.put("message", e.getMessage());
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
  }
}
