package com.dmware.api_onibusbh.services;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dmware.api_onibusbh.dto.OnibusDTO;
import com.dmware.api_onibusbh.entities.LinhaEntity;
import com.dmware.api_onibusbh.repositories.LinhasRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class OnibusService {

      @Autowired
      private LinhasRepository linhasRepository;
      @Autowired
      private ModelMapper modelMapper;

      ObjectMapper objectMapper = new ObjectMapper();

      public List<OnibusDTO> fetchOnibus() {
            List<LinhaEntity> linhas = linhasRepository.findAll();
            try {
                  // Lê o arquivo JSON e mapeia para uma lista de OnibusDTO
                  List<OnibusDTO> listOnibusDTOs = objectMapper.readValue(
                              new File("src/data/coordenadas/coordenadas.json"),
                              new TypeReference<List<OnibusDTO>>() {
                              });

                  return listOnibusDTOs;

            } catch (IOException e) {
                  e.printStackTrace();
                  return null;
            }
      }

}
