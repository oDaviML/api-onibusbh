package com.dmware.api_onibusbh.scheduler;

import com.dmware.api_onibusbh.services.LinhasService;
import org.slf4j.MDC;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

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
        try {
            MDC.put("transaction_id", UUID.randomUUID().toString());
            logger.info("Job agendado iniciado: Atualização de Coordenadas/Linhas.");
            linhasService.salvaLinhasNormais();
            logger.info("Job agendado finalizado: Atualização de Coordenadas/Linhas.");
        } finally {
            MDC.clear();
        }
    }
}
