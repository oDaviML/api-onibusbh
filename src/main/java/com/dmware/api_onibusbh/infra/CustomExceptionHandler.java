package com.dmware.api_onibusbh.infra;

import com.dmware.api_onibusbh.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.reactive.result.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.io.IOException;

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

    @ExceptionHandler(DicionarioNotFoundException.class)
    public ResponseEntity<ErrorResponse> dicionarioNotFoundException(DicionarioNotFoundException ex) {
        return ErrorResponse.of("Não foi encontrado o dicionário para os dados, por favor tente novamente",
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(LinhaNotFoundException.class)
    public ResponseEntity<ErrorResponse> linhaNotFoundException(LinhaNotFoundException ex) {
        return ErrorResponse.of("Não foi encontrada nenhuma linha, por favor tente novamente", HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ValidJsonException.class)
    public ResponseEntity<ErrorResponse> validJsonException(ValidJsonException ex) {
        return ErrorResponse.of(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> runtimeException(RuntimeException ex) {
        logger.error(ex.getMessage(), ex);
        return ErrorResponse.of("Ocorreu um erro interno, por favor tente novamente", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<ErrorResponse> ioException(IOException ex) {
        return ErrorResponse.of("Ocorreu um erro interno, por favor tente novamente", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> noResourceFoundException(NoResourceFoundException ex) {
        return ErrorResponse.of("Verifique a rota digitada ou os dados enviados", HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponse> noHandlerFoundException(NoHandlerFoundException ex) {
        return ErrorResponse.of("Verifique a rota digitada ou os dados enviados", HttpStatus.NOT_FOUND);
    }
}
