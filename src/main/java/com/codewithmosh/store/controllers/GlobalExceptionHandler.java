package com.codewithmosh.store.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        var error = new HashMap<String, String>();
        ex.getBindingResult().getFieldErrors().forEach((errorField) -> {
            error.put(errorField.getField(), errorField.getDefaultMessage());
        });
        return ResponseEntity.badRequest().body(error);
    }
}
