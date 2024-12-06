package com.dmware.api_onibusbh.infra;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.annotation.JsonFormat;

public record ErrorResponse(
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
        LocalDateTime timestamp,
        String message,
        HttpStatus status) {

    public static ResponseEntity<ErrorResponse> of(String message, HttpStatus status) {
        return new ResponseEntity<>(new ErrorResponse(LocalDateTime.now(), message, status), status);

    }
}