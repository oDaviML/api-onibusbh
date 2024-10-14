package com.dmware.api_onibusbh.services;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Service;

import com.dmware.api_onibusbh.dto.CoordenadaDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class CoordenadaService {
      ObjectMapper objectMapper = new ObjectMapper();

      public List<CoordenadaDTO> fetchCoordenadas() {
            try {
                  // Lê o arquivo JSON e mapeia para uma lista de coordenadaDTO
                  List<CoordenadaDTO> listCoordenadas = objectMapper.readValue(
                              new File("src/data/coordenadas/coordenadas.json"),
                              new TypeReference<List<CoordenadaDTO>>() {
                              });

                  return listCoordenadas;

            } catch (IOException e) {
                  e.printStackTrace();
                  return null;
            }
      }

}
