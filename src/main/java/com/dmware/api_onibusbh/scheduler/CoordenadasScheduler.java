package com.dmware.api_onibusbh.scheduler;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.dmware.api_onibusbh.dto.CoordenadaDTO;
import com.dmware.api_onibusbh.services.APIService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.dmware.api_onibusbh.services.OnibusService;

@Component
public class CoordenadasScheduler {

    private final APIService apiService;
    private final OnibusService onibusService;

    public CoordenadasScheduler(APIService apiService, OnibusService onibusService) {
        this.apiService = apiService;
        this.onibusService = onibusService;
    }

    @Async
    @Scheduled(fixedDelay = 20, timeUnit = TimeUnit.SECONDS)
    public void fetchCoordenadasOnibus() {
        List<CoordenadaDTO> coordenadas = apiService.getOnibusCoordenadaBH();
        onibusService.salvaCoordenadas(coordenadas);
    }

}
