package com.dmware.api_onibusbh.infra;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.reactive.result.method.annotation.ResponseEntityExceptionHandler;

import com.dmware.api_onibusbh.exceptions.CoordenadasNotFoundException;
import com.dmware.api_onibusbh.exceptions.LinhasNotFoundException;
import com.dmware.api_onibusbh.exceptions.ValidJsonException;

@ControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(LinhasNotFoundException.class)
    public ResponseEntity<ErrorResponse> linhasNotFoundException(LinhasNotFoundException ex) {
        return ErrorResponse.of("Não foi encontrada nenhuma linha, por favor tente novamente", HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(CoordenadasNotFoundException.class)
    public ResponseEntity<ErrorResponse> coordenadasNotFoundException(CoordenadasNotFoundException ex) {
        return ErrorResponse.of("Não foi encontrada nenhuma coordenada, por favor tente novamente",
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ValidJsonException.class)
    public ResponseEntity<ErrorResponse> validJsonException(ValidJsonException ex) {
        return ErrorResponse.of(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> runtimeException(RuntimeException ex) {
        return ErrorResponse.of("Ocorreu um erro interno, por favor tente novamente", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<ErrorResponse> ioException(IOException ex) {
        return ErrorResponse.of("Ocorreu um erro interno, por favor tente novamente", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
