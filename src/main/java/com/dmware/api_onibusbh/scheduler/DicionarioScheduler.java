package com.dmware.api_onibusbh.scheduler;

import com.dmware.api_onibusbh.services.DicionarioService;
import org.slf4j.MDC;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static net.logstash.logback.argument.StructuredArguments.kv;

@Component
public class DicionarioScheduler {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(DicionarioScheduler.class);
    private final DicionarioService dicionarioService;

    public DicionarioScheduler(DicionarioService dicionarioService) {
        this.dicionarioService = dicionarioService;
    }

    @Async
    @Scheduled(fixedDelay = 12, timeUnit = TimeUnit.HOURS)
    public void salvaDicionarioBanco() {
        long startTime = System.currentTimeMillis();
        try {
            MDC.put("transaction_id", UUID.randomUUID().toString());
            logger.info("Job de Dicionário iniciado.", kv("job_name", "Dicionario"), kv("status", "STARTED"));
            dicionarioService.salvarDicionarioBanco();
            long duration = System.currentTimeMillis() - startTime;
            logger.info("Job de Dicionário finalizado.", 
                kv("job_name", "Dicionario"), 
                kv("status", "FINISHED"), 
                kv("duration_ms", duration));
        } finally {
            MDC.clear();
        }
    }
}
