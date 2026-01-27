package com.dmware.api_onibusbh.infra;

import com.dmware.api_onibusbh.exceptions.*;
import com.dmware.api_onibusbh.utils.ClientIpUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.reactive.result.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.io.IOException;

import static net.logstash.logback.argument.StructuredArguments.kv;

@ControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(CustomExceptionHandler.class);

    @ExceptionHandler(LinhasNotFoundException.class)
    public ResponseEntity<ErrorResponse> linhasNotFoundException(LinhasNotFoundException ex, HttpServletRequest request) {
        logger.warn("Recurso não encontrado",
                kv("exception", ex.getClass().getSimpleName()),
                kv("status", HttpStatus.NOT_FOUND),
                kv("detail", ex.getMessage()),
                kv("path", request.getRequestURI()),
                kv("method", request.getMethod()),
                kv("client_ip", ClientIpUtils.getClientIp(request)));
        return ErrorResponse.of("Não foi encontrada nenhuma linha, por favor tente novamente", HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(CoordenadasNotFoundException.class)
    public ResponseEntity<ErrorResponse> coordenadasNotFoundException(CoordenadasNotFoundException ex, HttpServletRequest request) {
        logger.warn("Recurso não encontrado",
                kv("exception", ex.getClass().getSimpleName()),
                kv("status", HttpStatus.NOT_FOUND),
                kv("detail", ex.getMessage()),
                kv("path", request.getRequestURI()),
                kv("method", request.getMethod()),
                kv("client_ip", ClientIpUtils.getClientIp(request)));
        return ErrorResponse.of("Não foi encontrada nenhuma coordenada, por favor tente novamente",
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DicionarioNotFoundException.class)
    public ResponseEntity<ErrorResponse> dicionarioNotFoundException(DicionarioNotFoundException ex, HttpServletRequest request) {
        logger.warn("Recurso não encontrado",
                kv("exception", ex.getClass().getSimpleName()),
                kv("status", HttpStatus.NOT_FOUND),
                kv("detail", ex.getMessage()),
                kv("path", request.getRequestURI()),
                kv("method", request.getMethod()),
                kv("client_ip", ClientIpUtils.getClientIp(request)));
        return ErrorResponse.of("Não foi encontrado o dicionário para os dados, por favor tente novamente",
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(LinhaNotFoundException.class)
    public ResponseEntity<ErrorResponse> linhaNotFoundException(LinhaNotFoundException ex, HttpServletRequest request) {
        logger.warn("Recurso não encontrado",
                kv("exception", ex.getClass().getSimpleName()),
                kv("status", HttpStatus.NOT_FOUND),
                kv("detail", ex.getMessage()),
                kv("path", request.getRequestURI()),
                kv("method", request.getMethod()),
                kv("client_ip", ClientIpUtils.getClientIp(request)));
        return ErrorResponse.of("Não foi encontrada nenhuma linha, por favor tente novamente", HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ValidJsonException.class)
    public ResponseEntity<ErrorResponse> validJsonException(ValidJsonException ex, HttpServletRequest request) {
        logger.warn("Erro de validação JSON",
                kv("exception", ex.getClass().getSimpleName()),
                kv("status", HttpStatus.BAD_REQUEST),
                kv("detail", ex.getMessage()),
                kv("path", request.getRequestURI()),
                kv("method", request.getMethod()),
                kv("client_ip", ClientIpUtils.getClientIp(request)));
        return ErrorResponse.of(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> runtimeException(RuntimeException ex, HttpServletRequest request) {
        logger.error("Erro interno não tratado",
                kv("exception", ex.getClass().getSimpleName()),
                kv("status", HttpStatus.INTERNAL_SERVER_ERROR),
                kv("detail", ex.getMessage()),
                kv("path", request.getRequestURI()),
                kv("method", request.getMethod()),
                kv("client_ip", ClientIpUtils.getClientIp(request)),
                ex);
        return ErrorResponse.of("Ocorreu um erro interno, por favor tente novamente", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<ErrorResponse> ioException(IOException ex, HttpServletRequest request) {
        logger.error("Erro interno não tratado",
                kv("exception", ex.getClass().getSimpleName()),
                kv("status", HttpStatus.INTERNAL_SERVER_ERROR),
                kv("detail", ex.getMessage()),
                kv("path", request.getRequestURI()),
                kv("method", request.getMethod()),
                kv("client_ip", ClientIpUtils.getClientIp(request)),
                ex);
        return ErrorResponse.of("Ocorreu um erro interno, por favor tente novamente", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> noResourceFoundException(NoResourceFoundException ex, HttpServletRequest request) {
        logger.warn("Recurso não encontrado",
                kv("exception", ex.getClass().getSimpleName()),
                kv("status", HttpStatus.NOT_FOUND),
                kv("detail", ex.getMessage()),
                kv("path", request.getRequestURI()),
                kv("method", request.getMethod()),
                kv("client_ip", ClientIpUtils.getClientIp(request)));
        return ErrorResponse.of("Verifique a rota digitada ou os dados enviados", HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponse> noHandlerFoundException(NoHandlerFoundException ex, HttpServletRequest request) {
        logger.warn("Recurso não encontrado",
                kv("exception", ex.getClass().getSimpleName()),
                kv("status", HttpStatus.NOT_FOUND),
                kv("detail", ex.getMessage()),
                kv("path", request.getRequestURI()),
                kv("method", request.getMethod()),
                kv("client_ip", ClientIpUtils.getClientIp(request)));
        return ErrorResponse.of("Verifique a rota digitada ou os dados enviados", HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(RateLimitExceededException.class)
    public ResponseEntity<ErrorResponse> rateLimitExceededException(RateLimitExceededException ex, HttpServletRequest request) {
        logger.warn("Rate limit excedido",
                kv("exception", ex.getClass().getSimpleName()),
                kv("status", HttpStatus.TOO_MANY_REQUESTS),
                kv("retry_after", ex.getRetryAfterSeconds()),
                kv("path", request.getRequestURI()),
                kv("method", request.getMethod()),
                kv("client_ip", ClientIpUtils.getClientIp(request)));
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .header("X-Rate-Limit-Retry-After-Seconds", String.valueOf(ex.getRetryAfterSeconds()))
                .body(new ErrorResponse(java.time.LocalDateTime.now(), ex.getMessage(), HttpStatus.TOO_MANY_REQUESTS));
    }

}
