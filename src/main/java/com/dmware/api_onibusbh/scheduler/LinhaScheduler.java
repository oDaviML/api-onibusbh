package com.dmware.api_onibusbh.scheduler;

import com.dmware.api_onibusbh.services.LinhasService;
import org.slf4j.MDC;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static net.logstash.logback.argument.StructuredArguments.kv;

@Component
public class LinhaScheduler {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(LinhaScheduler.class);
    private final LinhasService linhasService;

    public LinhaScheduler(LinhasService linhasService) {
        this.linhasService = linhasService;
    }

    @Async
    @Scheduled(fixedDelay = 12, timeUnit = TimeUnit.HOURS)
    public void fetchCoordenadasOnibus() {
        long startTime = System.currentTimeMillis();
        try {
            MDC.put("transaction_id", UUID.randomUUID().toString());
            logger.info("Job agendado iniciado: Atualização de Coordenadas/Linhas.", kv("job_name", "Linhas"), kv("status", "STARTED"));
            linhasService.salvaLinhasNormais();
            long duration = System.currentTimeMillis() - startTime;
            logger.info("Job agendado finalizado: Atualização de Coordenadas/Linhas.", 
                kv("job_name", "Linhas"), 
                kv("status", "FINISHED"), 
                kv("duration_ms", duration));
        } finally {
            MDC.clear();
        }
    }
}
