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

import com.dmware.api_onibusbh.exceptions.CoordenadasApiIntegrationException;

@ControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(CustomExceptionHandler.class);

    @ExceptionHandler(LinhasNotFoundException.class)
    public ResponseEntity<ErrorResponse> linhasNotFoundException(LinhasNotFoundException ex, HttpServletRequest request) {
        String clientIp = ClientIpUtils.getClientIp(request);
        logger.warn("Recurso não encontrado | {} {} | IP: {} | Detalhe: {}",
                request.getMethod(), request.getRequestURI(), clientIp, ex.getMessage(),
                kv("exception", ex.getClass().getSimpleName()),
                kv("status", HttpStatus.NOT_FOUND),
                kv("detail", ex.getMessage()),
                kv("path", request.getRequestURI()),
                kv("method", request.getMethod()),
                kv("client_ip", clientIp));
        return ErrorResponse.of("Não foi encontrada nenhuma linha, por favor tente novamente", HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(CoordenadasNotFoundException.class)
    public ResponseEntity<ErrorResponse> coordenadasNotFoundException(CoordenadasNotFoundException ex, HttpServletRequest request) {
        String clientIp = ClientIpUtils.getClientIp(request);
        logger.warn("Recurso não encontrado | {} {} | IP: {} | Detalhe: {}",
                request.getMethod(), request.getRequestURI(), clientIp, ex.getMessage(),
                kv("exception", ex.getClass().getSimpleName()),
                kv("status", HttpStatus.NOT_FOUND),
                kv("detail", ex.getMessage()),
                kv("path", request.getRequestURI()),
                kv("method", request.getMethod()),
                kv("client_ip", clientIp));
        return ErrorResponse.of("Não foi encontrada nenhuma coordenada, por favor tente novamente",
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DicionarioNotFoundException.class)
    public ResponseEntity<ErrorResponse> dicionarioNotFoundException(DicionarioNotFoundException ex, HttpServletRequest request) {
        String clientIp = ClientIpUtils.getClientIp(request);
        logger.warn("Recurso não encontrado | {} {} | IP: {} | Detalhe: {}",
                request.getMethod(), request.getRequestURI(), clientIp, ex.getMessage(),
                kv("exception", ex.getClass().getSimpleName()),
                kv("status", HttpStatus.NOT_FOUND),
                kv("detail", ex.getMessage()),
                kv("path", request.getRequestURI()),
                kv("method", request.getMethod()),
                kv("client_ip", clientIp));
        return ErrorResponse.of("Não foi encontrado o dicionário para os dados, por favor tente novamente",
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(LinhaNotFoundException.class)
    public ResponseEntity<ErrorResponse> linhaNotFoundException(LinhaNotFoundException ex, HttpServletRequest request) {
        String clientIp = ClientIpUtils.getClientIp(request);
        logger.warn("Recurso não encontrado | {} {} | IP: {} | Detalhe: {}",
                request.getMethod(), request.getRequestURI(), clientIp, ex.getMessage(),
                kv("exception", ex.getClass().getSimpleName()),
                kv("status", HttpStatus.NOT_FOUND),
                kv("detail", ex.getMessage()),
                kv("path", request.getRequestURI()),
                kv("method", request.getMethod()),
                kv("client_ip", clientIp));
        return ErrorResponse.of("Não foi encontrada nenhuma linha, por favor tente novamente", HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ValidJsonException.class)
    public ResponseEntity<ErrorResponse> validJsonException(ValidJsonException ex, HttpServletRequest request) {
        String clientIp = ClientIpUtils.getClientIp(request);
        logger.warn("Erro de validação JSON | {} {} | IP: {} | Detalhe: {}",
                request.getMethod(), request.getRequestURI(), clientIp, ex.getMessage(),
                kv("exception", ex.getClass().getSimpleName()),
                kv("status", HttpStatus.BAD_REQUEST),
                kv("detail", ex.getMessage()),
                kv("path", request.getRequestURI()),
                kv("method", request.getMethod()),
                kv("client_ip", clientIp));
        return ErrorResponse.of(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> runtimeException(RuntimeException ex, HttpServletRequest request) {
        String clientIp = ClientIpUtils.getClientIp(request);
        logger.error("Erro interno não tratado | {} {} | IP: {} | Detalhe: {}",
                request.getMethod(), request.getRequestURI(), clientIp, ex.getMessage(),
                kv("exception", ex.getClass().getSimpleName()),
                kv("status", HttpStatus.INTERNAL_SERVER_ERROR),
                kv("detail", ex.getMessage()),
                kv("path", request.getRequestURI()),
                kv("method", request.getMethod()),
                kv("client_ip", clientIp),
                ex);
        return ErrorResponse.of("Ocorreu um erro interno, por favor tente novamente", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<ErrorResponse> ioException(IOException ex, HttpServletRequest request) {
        String clientIp = ClientIpUtils.getClientIp(request);
        logger.error("Erro interno não tratado | {} {} | IP: {} | Detalhe: {}",
                request.getMethod(), request.getRequestURI(), clientIp, ex.getMessage(),
                kv("exception", ex.getClass().getSimpleName()),
                kv("status", HttpStatus.INTERNAL_SERVER_ERROR),
                kv("detail", ex.getMessage()),
                kv("path", request.getRequestURI()),
                kv("method", request.getMethod()),
                kv("client_ip", clientIp),
                ex);
        return ErrorResponse.of("Ocorreu um erro interno, por favor tente novamente", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> noResourceFoundException(NoResourceFoundException ex, HttpServletRequest request) {
        String clientIp = ClientIpUtils.getClientIp(request);
        logger.warn("Recurso não encontrado | {} {} | IP: {} | Detalhe: {}",
                request.getMethod(), request.getRequestURI(), clientIp, ex.getMessage(),
                kv("exception", ex.getClass().getSimpleName()),
                kv("status", HttpStatus.NOT_FOUND),
                kv("detail", ex.getMessage()),
                kv("path", request.getRequestURI()),
                kv("method", request.getMethod()),
                kv("client_ip", clientIp));
        return ErrorResponse.of("Verifique a rota digitada ou os dados enviados", HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponse> noHandlerFoundException(NoHandlerFoundException ex, HttpServletRequest request) {
        String clientIp = ClientIpUtils.getClientIp(request);
        logger.warn("Recurso não encontrado | {} {} | IP: {} | Detalhe: {}",
                request.getMethod(), request.getRequestURI(), clientIp, ex.getMessage(),
                kv("exception", ex.getClass().getSimpleName()),
                kv("status", HttpStatus.NOT_FOUND),
                kv("detail", ex.getMessage()),
                kv("path", request.getRequestURI()),
                kv("method", request.getMethod()),
                kv("client_ip", clientIp));
        return ErrorResponse.of("Verifique a rota digitada ou os dados enviados", HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(RateLimitExceededException.class)
    public ResponseEntity<ErrorResponse> rateLimitExceededException(RateLimitExceededException ex, HttpServletRequest request) {
        String clientIp = ClientIpUtils.getClientIp(request);
        logger.warn("Rate limit excedido | {} {} | IP: {} | Retry-After: {}s",
                request.getMethod(), request.getRequestURI(), clientIp, ex.getRetryAfterSeconds(),
                kv("exception", ex.getClass().getSimpleName()),
                kv("status", HttpStatus.TOO_MANY_REQUESTS),
                kv("retry_after", ex.getRetryAfterSeconds()),
                kv("path", request.getRequestURI()),
                kv("method", request.getMethod()),
                kv("client_ip", clientIp));
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .header("X-Rate-Limit-Retry-After-Seconds", String.valueOf(ex.getRetryAfterSeconds()))
                .body(new ErrorResponse(java.time.LocalDateTime.now(), ex.getMessage(), HttpStatus.TOO_MANY_REQUESTS));
    }

    @ExceptionHandler(CoordenadasApiIntegrationException.class)
    public ResponseEntity<ErrorResponse> coordenadasApiIntegrationException(CoordenadasApiIntegrationException ex, HttpServletRequest request) {
        String clientIp = ClientIpUtils.getClientIp(request);
        logger.error("Falha na integração com API externa | {} {} | IP: {} | Endpoint: {} | Tipo: {} | Mensagem: {}",
                request.getMethod(), request.getRequestURI(), clientIp, ex.getEndpoint(), ex.getErrorType(), ex.getMessage(),
                kv("exception", ex.getClass().getSimpleName()),
                kv("status", HttpStatus.SERVICE_UNAVAILABLE),
                kv("endpoint", ex.getEndpoint()),
                kv("error_type", ex.getErrorType()),
                kv("detail", ex.getMessage()),
                kv("path", request.getRequestURI()),
                kv("method", request.getMethod()),
                kv("client_ip", clientIp),
                ex);
        return ErrorResponse.of("Falha na comunicação com serviço externo. Os dados existentes foram preservados.", HttpStatus.SERVICE_UNAVAILABLE);
    }

}
