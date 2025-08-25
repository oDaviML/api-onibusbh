package com.dmware.api_onibusbh.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.dmware.api_onibusbh.config.WebClientConfig;
import com.dmware.api_onibusbh.dto.DicionarioDTO;
import com.dmware.api_onibusbh.dto.LinhaDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import reactor.core.publisher.Flux;

@Service
public class APIService {

    @Value("${BASE_PATH}")
    private String BASE_PATH;
    @Value("${FILE_NAME}")
    private String FILE_NAME;

    private final WebClientConfig webClientConfig;
    private final ObjectMapper objectMapper;

    public APIService(WebClientConfig webClientConfig, ObjectMapper objectMapper) {
        this.webClientConfig = webClientConfig;
        this.objectMapper = objectMapper;
    }

    public List<DicionarioDTO> getDicionarioAPIBH() {
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

        } catch (JsonProcessingException | WebClientResponseException e) {
            throw new RuntimeException(e);
        }

        return dicionarios;
    }

    public List<LinhaDTO> getLinhasAPIBH() {
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

            return objectMapper.readValue(recordsNode.toString(),
                    new TypeReference<List<LinhaDTO>>() {
                    });

        } catch (JsonProcessingException | WebClientResponseException e) {
            throw new RuntimeException(e);
        }
    }

    public void getOnibusCoordenadaBH() throws IOException {
        Flux<DataBuffer> dataBufferFlux = webClientConfig.webClient().get()
                .uri("https://temporeal.pbh.gov.br/?param=D")
                .retrieve()
                .bodyToFlux(DataBuffer.class);
        if (!Files.exists(Paths.get(BASE_PATH))) {
            Files.createDirectories(Paths.get(BASE_PATH));
        }
        DataBufferUtils.write(dataBufferFlux, Paths.get(BASE_PATH, FILE_NAME), StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING).block();
    }


}
