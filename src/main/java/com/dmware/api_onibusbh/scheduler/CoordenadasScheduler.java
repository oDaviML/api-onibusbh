package com.dmware.api_onibusbh.scheduler;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.dmware.api_onibusbh.services.APIService;

@Component
public class CoordenadasScheduler {

      @Autowired
      private APIService apiService;

      @Scheduled(fixedDelay = 20, timeUnit = TimeUnit.SECONDS)
      public void fetchCoordenadasOnibus() throws IOException {
            apiService.getOnibusCoordenadaBH();
      }

}
