// package com.example.poc_finops.config;

// import lombok.extern.slf4j.Slf4j;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.MethodArgumentNotValidException;
// import org.springframework.web.bind.annotation.*;

// import java.util.HashMap;
// import java.util.Map;

// @Slf4j
// @RestControllerAdvice
// public class GlobalExceptionHandler {

//     @ExceptionHandler(Exception.class)
//     public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
//         log.error("Unhandled exception occurred: {}", ex.getMessage(), ex);
//         Map<String, Object> body = new HashMap<>();
//         body.put("error", "Internal Server Error");
//         body.put("message", ex.getMessage());
//         return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
//     }

//     @ExceptionHandler(MethodArgumentNotValidException.class)
//     public ResponseEntity<Map<String, Object>> handleValidationException(MethodArgumentNotValidException ex) {
//         log.warn("Validation failed: {}", ex.getMessage());
//         Map<String, Object> errors = new HashMap<>();
//         ex.getBindingResult().getFieldErrors().forEach(error ->
//             errors.put(error.getField(), error.getDefaultMessage())
//         );
//         return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
//     }

//     // You can add more specific handlers as needed.
// }
