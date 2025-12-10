package com.dmware.api_onibusbh.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
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
import reactor.core.publisher.Mono;

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
        System.out.printf("Acessando diretamente.%n");
        List<String> responses = fetchCoordenadasDirectly();

        if (responses == null || responses.size() < 2) {
            throw new IllegalStateException("Não foi possível obter resposta de um ou mais endpoints.");
        }

        processAndSaveCoordenadas(responses);
    }

    private List<String> fetchCoordenadasDirectly() {
        Mono<String> monoParamD = webClientConfig.webClient().get()
                .uri("https://temporeal.pbh.gov.br/?param=D")
                .retrieve()
                .bodyToMono(String.class);

        Mono<String> monoParamSD = webClientConfig.webClient().get()
                .uri("https://temporeal.pbh.gov.br/?param=SD")
                .retrieve()
                .bodyToMono(String.class);

        return Flux.merge(monoParamD, monoParamSD).collectList().block();
    }

    private void processAndSaveCoordenadas(List<String> responses) throws IOException {
        String jsonD = responses.get(0);
        String jsonSD = responses.get(1);

        try {
            TypeReference<List<Map<String, Object>>> typeRef = new TypeReference<>() {
            };
            List<Map<String, Object>> listD = objectMapper.readValue(jsonD, typeRef);
            List<Map<String, Object>> listSD = objectMapper.readValue(jsonSD, typeRef);

            List<Map<String, Object>> combinedList = new ArrayList<>(listD);
            combinedList.addAll(listSD);

            String finalJson = objectMapper.writeValueAsString(combinedList);

            Path basePath = Paths.get(BASE_PATH);
            if (!Files.exists(basePath)) {
                Files.createDirectories(basePath);
            }

            Path filePath = basePath.resolve(FILE_NAME);
            Files.writeString(filePath, finalJson);

        } catch (JsonProcessingException e) {
            throw new RuntimeException("Erro ao processar o JSON das coordenadas.", e);
        }
    }
}