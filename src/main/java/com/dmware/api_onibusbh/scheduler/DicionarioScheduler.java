package com.dmware.api_onibusbh.scheduler;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.dmware.api_onibusbh.services.DicionarioService;

@Component
public class DicionarioScheduler {

      @Autowired
      private DicionarioService dicionarioService;

      @Async
      @Scheduled(fixedDelay = 12, timeUnit = TimeUnit.HOURS)
      public void salvaDicionarioBanco() {
            dicionarioService.salvarDicionarioBanco();
      }
}
