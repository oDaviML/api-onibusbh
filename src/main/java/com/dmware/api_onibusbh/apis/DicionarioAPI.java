package com.dmware.api_onibusbh.apis;

import com.dmware.api_onibusbh.config.WebClientConfig;
import com.dmware.api_onibusbh.dto.DicionarioDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

public class DicionarioAPI {

    @Autowired
    private WebClientConfig webClientConfig;

    ObjectMapper objectMapper = new ObjectMapper();

    @Bean
    public List<DicionarioDTO> fetchDicionario() {
        List<DicionarioDTO> dicionarios;
        try {
            String json = webClientConfig.webClient().get()
                    .uri("https://ckan.pbh.gov.br/api/3/action/datastore_search?resource_id=825337e5-8cd5-43d9-ac52-837d80346721&limit=20")
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            JsonNode rootNode = objectMapper.readTree(json);
            JsonNode recordsNode = rootNode.path("result").path("records");

            // Deserializa o JsonNode em uma lista de objetos DicionarioDTO
            dicionarios = objectMapper.readValue(recordsNode.toString(),
                    new TypeReference<List<DicionarioDTO>>() {
                    });

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return dicionarios;
    }

}
