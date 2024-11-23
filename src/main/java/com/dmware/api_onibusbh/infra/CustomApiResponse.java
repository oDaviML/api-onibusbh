package com.dmware.api_onibusbh.infra;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public record CustomApiResponse<T>(LocalDateTime timestamp, String message, HttpStatus status, T data) {
    public static <T> ResponseEntity<CustomApiResponse<T>> of(String message, HttpStatus status, T data) {
        return new ResponseEntity<>(new CustomApiResponse<>(LocalDateTime.now(), message, status, data), status);
    }
}