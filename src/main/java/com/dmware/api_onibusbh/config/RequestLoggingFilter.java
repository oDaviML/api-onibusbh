package com.dmware.api_onibusbh.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

import static net.logstash.logback.argument.StructuredArguments.kv;

@Component
public class RequestLoggingFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(RequestLoggingFilter.class);
    private static final String TRANSACTION_ID_KEY = "transaction_id";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        long startTime = System.currentTimeMillis();

        String transactionId = request.getHeader("X-Transaction-ID");
        if (transactionId == null || transactionId.isEmpty()) {
            transactionId = UUID.randomUUID().toString();
        }

        MDC.put(TRANSACTION_ID_KEY, transactionId);

        try {
            logger.info("Requisição recebida: {} {}", request.getMethod(), request.getRequestURI(),
                    kv("method", request.getMethod()),
                    kv("uri", request.getRequestURI()),
                    kv("client_ip", request.getRemoteAddr()),
                    kv("user_agent", request.getHeader("User-Agent"))
            );

            filterChain.doFilter(request, response);

            long duration = System.currentTimeMillis() - startTime;

            logger.info("Requisição finalizada: {} {} - Status: {} - Tempo: {}ms",
                    request.getMethod(),
                    request.getRequestURI(),
                    response.getStatus(),
                    duration,
                    kv("status_code", response.getStatus()),
                    kv("duration_ms", duration)
            );

        } finally {
            MDC.remove(TRANSACTION_ID_KEY);
        }
    }
}
