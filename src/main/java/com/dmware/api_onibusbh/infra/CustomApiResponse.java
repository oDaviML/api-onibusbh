package com.dmware.api_onibusbh.infra;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.annotation.JsonFormat;

public record CustomApiResponse<T>(
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss:SSS") LocalDateTime timestamp,
        String message,
        HttpStatus status,
        T data) {
    public static <T> ResponseEntity<CustomApiResponse<T>> of(String message, HttpStatus status, T data) {
        return new ResponseEntity<>(new CustomApiResponse<>(LocalDateTime.now(), message, status, data), status);
    }
}