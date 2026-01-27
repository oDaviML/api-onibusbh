package com.dmware.api_onibusbh.scheduler;

import com.dmware.api_onibusbh.dto.CoordenadaDTO;
import com.dmware.api_onibusbh.services.APIService;
import com.dmware.api_onibusbh.services.OnibusService;
import org.slf4j.MDC;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static net.logstash.logback.argument.StructuredArguments.kv;

@Component
public class CoordenadasScheduler {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(CoordenadasScheduler.class);
    private final APIService apiService;
    private final OnibusService onibusService;

    public CoordenadasScheduler(APIService apiService, OnibusService onibusService) {
        this.apiService = apiService;
        this.onibusService = onibusService;
    }

    @Scheduled(fixedDelay = 20, timeUnit = TimeUnit.SECONDS)
    public void fetchCoordenadasOnibus() {
        long startTime = System.currentTimeMillis();
        try {
            MDC.put("transaction_id", UUID.randomUUID().toString());
            logger.info("Job de Coordenadas iniciado.", kv("job_name", "Coordenadas"), kv("status", "STARTED"));
            List<CoordenadaDTO> coordenadas = apiService.getOnibusCoordenadaBH();
            onibusService.salvaCoordenadas(coordenadas);
            long duration = System.currentTimeMillis() - startTime;
            logger.info("Job de Coordenadas finalizado | Tempo: {}ms", duration,
                kv("job_name", "Coordenadas"), 
                kv("status", "FINISHED"), 
                kv("duration_ms", duration));
        } finally {
            MDC.clear();
        }
    }
}
