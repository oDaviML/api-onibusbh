package com.dmware.api_onibusbh.scheduler;

import com.dmware.api_onibusbh.services.LinhasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class LinhaScheduler {

    @Autowired
    private LinhasService linhasService;

    @Scheduled(fixedDelay = 12, timeUnit = TimeUnit.HOURS)
    public void fetchCoordenadasOnibus() {
        linhasService.salvaLinhasBanco();
    }

}
