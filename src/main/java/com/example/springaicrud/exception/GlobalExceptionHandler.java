package com.example.springaicrud.exception;

import org.springframework.http.*;
import org.springframework.validation.FieldError;
import org.springframework.web.bind
        .MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // ✅ Validation errors
    // e.g. @Email, @NotBlank, @Size fails
    @ExceptionHandler(
            MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>>
    handleValidation(
            MethodArgumentNotValidException
                    ex) {
        Map<String, Object> errors =
                new LinkedHashMap<>();
        errors.put("status", 400);

        List<String> messages = new ArrayList<>();
        for (FieldError fe :
                ex.getBindingResult()
                        .getFieldErrors()) {
            messages.add(fe.getField()
                    + ": "
                    + fe.getDefaultMessage());
        }
        errors.put("errors", messages);
        errors.put("timestamp", new Date());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errors);
    }

    // ✅ Runtime errors
    // e.g. Email not found, Wrong password
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>>
    handleRuntime(RuntimeException ex) {

        // ✅ Check if it is rate limit error
        // from RateLimitFilter message
        if (ex.getMessage() != null
                && ex.getMessage().contains(
                "Rate limit")) {

            Map<String, Object> error =
                    new LinkedHashMap<>();
            error.put("status", 429);
            error.put("error",
                    "Too Many Requests");
            error.put("message",
                    ex.getMessage());
            error.put("timestamp", new Date());

            return ResponseEntity
                    .status(
                            HttpStatus.TOO_MANY_REQUESTS)
                    .body(error);
        }

        Map<String, Object> error =
                new LinkedHashMap<>();
        error.put("status", 400);
        error.put("message", ex.getMessage());
        error.put("timestamp", new Date());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(error);
    }

    // ✅ All other errors
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>>
    handleAll(Exception ex) {

        Map<String, Object> error =
                new LinkedHashMap<>();
        error.put("status", 500);
        error.put("message", ex.getMessage());
        error.put("timestamp", new Date());

        return ResponseEntity
                .status(
                        HttpStatus.INTERNAL_SERVER_ERROR)
                .body(error);
    }
}