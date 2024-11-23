package com.dmware.api_onibusbh.utils;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public record ApiResponse<T>(LocalDateTime timestamp, String message, HttpStatus status, T data) {
    public static <T> ResponseEntity<ApiResponse<T>> of(String message, HttpStatus status, T data) {
        return new ResponseEntity<>(new ApiResponse<>(LocalDateTime.now(), message, status, data), status);
    }
}