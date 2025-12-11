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

    private final DicionarioService dicionarioService;

    public DicionarioScheduler(DicionarioService dicionarioService) {
        this.dicionarioService = dicionarioService;
    }

    @Async
      @Scheduled(fixedDelay = 12, timeUnit = TimeUnit.HOURS)
      public void salvaDicionarioBanco() {
            try {
                  MDC.put("transaction_id", UUID.randomUUID().toString());
                  dicionarioService.salvarDicionarioBanco();
            } finally {
                  MDC.clear();
            }
      }
}
