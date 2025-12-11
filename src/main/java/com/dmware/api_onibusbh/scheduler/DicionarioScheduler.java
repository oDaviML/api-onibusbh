package com.dmware.api_onibusbh.scheduler;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.dmware.api_onibusbh.services.DicionarioService;

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
            try {
                  MDC.put("transaction_id", UUID.randomUUID().toString());
                  logger.info("Job de Dicionário iniciado.");
                  dicionarioService.salvarDicionarioBanco();
                  logger.info("Job de Dicionário finalizado.");
            } finally {
                  MDC.clear();
            }
      }
}
