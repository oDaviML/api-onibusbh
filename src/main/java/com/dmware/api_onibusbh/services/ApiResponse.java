package com.dmware.api_onibusbh.services;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public record ApiResponse<T>(
    String message,
    HttpStatus status,
    T data
) {
    public static <T> ResponseEntity<ApiResponse<T>> of(
        String message,
        HttpStatus status,
        T data
    ) {
        return new ResponseEntity<>(
            new ApiResponse<>(message, status, data),
            status
        );
    }
}