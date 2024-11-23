package com.dmware.api_onibusbh.infra;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public record ErrorResponse(LocalDateTime timestamp, String message, HttpStatus status) {
    public static ResponseEntity<ErrorResponse> of(String message, HttpStatus status) {
        return new ResponseEntity<>(new ErrorResponse(LocalDateTime.now(), message, status), status);
    }
}