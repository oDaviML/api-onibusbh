package com.dmware.api_onibusbh.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .defaultHeaders(headers -> {
                    headers.add(HttpHeaders.USER_AGENT, "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");
                    headers.add(HttpHeaders.ACCEPT, "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
                    headers.add(HttpHeaders.ACCEPT_LANGUAGE, "en-US,en;q=0.9");
                    headers.add(HttpHeaders.ACCEPT_ENCODING, "gzip, deflate, br");
                    headers.add(HttpHeaders.CONNECTION, "keep-alive");
                })
                .build();
    }
}