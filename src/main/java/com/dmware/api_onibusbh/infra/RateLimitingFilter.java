package com.dmware.api_onibusbh.infra;

import com.dmware.api_onibusbh.exceptions.RateLimitExceededException;
import com.dmware.api_onibusbh.utils.ClientIpUtils;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Component
public class RateLimitingFilter extends OncePerRequestFilter {

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    @Autowired
    private HandlerExceptionResolver handlerExceptionResolver;

    @Value("${rate.limit.capacity:60}")
    private long capacity;

    @Value("${rate.limit.tokens:60}")
    private long tokens;

    @Value("${rate.limit.duration-seconds:60}")
    private long durationSeconds;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String clientIp = ClientIpUtils.getClientIp(request);
        Bucket bucket = buckets.computeIfAbsent(clientIp, this::createNewBucket);

        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);

        if (probe.isConsumed()) {
            response.setHeader("X-Rate-Limit-Remaining", String.valueOf(probe.getRemainingTokens()));
            filterChain.doFilter(request, response);
        } else {
            long waitForRefill = probe.getNanosToWaitForRefill();
            long retryAfterSeconds = TimeUnit.NANOSECONDS.toSeconds(waitForRefill);

            if (waitForRefill % TimeUnit.SECONDS.toNanos(1) != 0) {
                retryAfterSeconds++;
            }

            handlerExceptionResolver.resolveException(request, response, null,
                    new RateLimitExceededException(
                            "Rate limit exceeded. Try again in " + retryAfterSeconds + " seconds.",
                            retryAfterSeconds
                    ));
        }
    }

    private Bucket createNewBucket(String key) {
        return Bucket.builder()
                .addLimit(limit -> limit.capacity(capacity).refillIntervally(tokens, Duration.ofSeconds(durationSeconds)))
                .build();
    }
}
