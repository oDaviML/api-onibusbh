package com.dmware.api_onibusbh.scheduler;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

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

      private static final Logger logger = LoggerFactory.getLogger(CoordenadasScheduler.class);

      @Scheduled(fixedDelay = 20, timeUnit = TimeUnit.SECONDS)
      public void fetchCoordenadasOnibus() throws IOException {
            logger.info("Iniciando sincronização de coordenadas");

            // Consome API PBH para buscar coordenadas e realizar o download do arquivo
            // .json
            Flux<DataBuffer> dataBufferFlux = webClientConfig.webClient().get()
                        .uri("https://temporeal.pbh.gov.br/?param=D")
                        .retrieve()
                        .bodyToFlux(DataBuffer.class);

            Path directory = Paths.get("src/data/coordenadas");

            // Verifica se o diretório de destino existe, se não, cria-o
            if (Files.notExists(directory)) {
                  Files.createDirectories(directory);
            }

            Path path = directory.resolve("coordenadas.json");

            try (FileOutputStream outputStream = new FileOutputStream(path.toFile())) {
                  // Converte os dados do buffer para bytes e escreve no arquivo
                  DataBufferUtils.write(dataBufferFlux, outputStream.getChannel())
                              .doOnComplete(() -> logger.info("Sincronização de coordenadas concluída"))
                              .blockLast(); // Garantir que o processo seja bloqueante e termine antes de prosseguir
            } catch (IOException e) {
                  logger.error("Erro ao tentar sincronizar as coordenadas", e);
            }
      }

}
