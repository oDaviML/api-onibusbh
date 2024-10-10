package com.dmware.api_onibusbh.scheduler;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

import com.dmware.api_onibusbh.services.APIService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.dmware.api_onibusbh.config.WebClientConfig;

import reactor.core.publisher.Flux;

@Component
public class CoordenadasScheduler {

      @Autowired
      private WebClientConfig webClientConfig;

      @Autowired
      private APIService apiService;

      private static final Logger logger = LoggerFactory.getLogger(CoordenadasScheduler.class);

      @Scheduled(fixedDelay = 20, timeUnit = TimeUnit.SECONDS)
      public void fetchCoordenadasOnibus() throws IOException {
            apiService.getOnibusCoordenadaBH();
      }

}
