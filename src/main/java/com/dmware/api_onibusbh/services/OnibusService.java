package com.dmware.api_onibusbh.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dmware.api_onibusbh.config.WebClientConfig;
import com.dmware.api_onibusbh.dto.DicionarioDTO;
import com.dmware.api_onibusbh.dto.OnibusDTO;
import com.dmware.api_onibusbh.entities.LinhaEntity;
import com.dmware.api_onibusbh.repositories.LinhasRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class OnibusService {

      @Autowired
      private LinhasRepository linhasRepository;

      @Autowired
      private WebClientConfig webClientConfig;

      ObjectMapper objectMapper = new ObjectMapper();

      public List<OnibusDTO> fetchOnibus() {
            List<LinhaEntity> linhas = linhasRepository.findAll();

            String json = webClientConfig.webClient().get()
                        .uri("https://ckan.pbh.gov.br/api/3/action/datastore_search?resource_id=825337e5-8cd5-43d9-ac52-837d80346721&limit=20")
                        .retrieve()
                        .bodyToMono(String.class)
                        .block();

            try {
                  // Lê o json e navega até o campo "result" -> "records"
                  JsonNode rootNode = objectMapper.readTree(json);
                  JsonNode recordsNode = rootNode.path("result").path("records");

                  // Deserializa o JsonNode em uma lista de objetos DicionarioDTO
                  // Deserializa o JsonNode em uma lista de objetos LinhaDTO
                  List<DicionarioDTO> dicionario = objectMapper.readValue(recordsNode.toString(),
                              new TypeReference<List<DicionarioDTO>>() {
                              });

            } catch (JsonProcessingException e) {

                  e.printStackTrace();
            }

            return null;
      }

}
