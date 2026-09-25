package com.codewithmosh.store.controllers;

import com.codewithmosh.store.dtos.ErrorDto;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorDto> handelUnreadableMessage(){
        return ResponseEntity.badRequest().body(
                new ErrorDto("Invalid request body")
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        var error = new HashMap<String, String>();
        ex.getBindingResult().getFieldErrors().forEach((errorField) -> {
            error.put(errorField.getField(), errorField.getDefaultMessage());
        });
        return ResponseEntity.badRequest().body(error);
    }
}
