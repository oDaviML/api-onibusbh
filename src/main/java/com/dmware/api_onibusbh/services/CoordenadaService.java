package com.dmware.api_onibusbh.services;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.dmware.api_onibusbh.dto.CoordenadaDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class CoordenadaService {

      ObjectMapper objectMapper = new ObjectMapper();
      private static final Logger logger = LoggerFactory.getLogger(CoordenadaService.class);

      private static final String BASE_PATH = "src/data/coordenadas";
      private static final String FILE_NAME = "coordenadas.json";

      public List<CoordenadaDTO> fetchCoordenadas() {
            try {
                  Path file = Paths.get(BASE_PATH, FILE_NAME);
                  if (!Files.exists(file)) {
                        logger.warn("Arquivo de coordenadas não encontrado");
                        return Collections.emptyList();
                  }

                  List<CoordenadaDTO> coordenadas = objectMapper.readValue(
                              file.toFile(),
                              new TypeReference<List<CoordenadaDTO>>() {
                              });

                  logger.info("Lidas {} coordenadas do arquivo", coordenadas.size());
                  return coordenadas;

            } catch (IOException e) {
                  logger.error("Erro ao ler arquivo de coordenadas", e);
                  return Collections.emptyList();
            }
      }

}
