package com.dmware.api_onibusbh.apis;

import com.dmware.api_onibusbh.config.WebClientConfig;
import com.dmware.api_onibusbh.dto.LinhaDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;

import java.util.List;

public class LinhasAPI {

    @Autowired
    private WebClientConfig webClientConfig;

    ObjectMapper objectMapper = new ObjectMapper();

    private static final Logger logger = LoggerFactory.getLogger(LinhasAPI.class);

    @Bean
    public List<LinhaDTO> fetchLinhas() {
        try {
            // Consome API PBH para buscar linhas
            String json = webClientConfig.webClient().get()
                    .uri("https://ckan.pbh.gov.br/api/3/action/datastore_search?resource_id=150bddd0-9a2c-4731-ade9-54aa56717fb6&limit=3000")
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();


            // Lê o json e navega até o campo "result" -> "records"
            JsonNode rootNode = objectMapper.readTree(json);
            JsonNode recordsNode = rootNode.path("result").path("records");

            // Deserializa o JsonNode em uma lista de objetos LinhaDTO
            List<LinhaDTO> linhasNovas = objectMapper.readValue(recordsNode.toString(),
                    new TypeReference<List<LinhaDTO>>() {
                    });

            return linhasNovas;

        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

}
