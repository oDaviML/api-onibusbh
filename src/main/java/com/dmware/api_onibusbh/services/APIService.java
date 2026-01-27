package com.dmware.api_onibusbh.services;

import java.util.ArrayList;
import java.util.List;

import com.dmware.api_onibusbh.dto.CoordenadaDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

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

    private static final Logger logger = LoggerFactory.getLogger(APIService.class);

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public APIService(WebClient webClient, ObjectMapper objectMapper) {
        this.webClient = webClient;
        this.objectMapper = objectMapper;
    }

    public List<DicionarioDTO> getDicionarioAPIBH() {
        try {
            String json = webClient.get()
                    .uri("https://ckan.pbh.gov.br/api/3/action/datastore_search?resource_id=825337e5-8cd5-43d9-ac52-837d80346721&limit=20")
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            JsonNode rootNode = objectMapper.readTree(json);
            JsonNode recordsNode = rootNode.path("result").path("records");

            return objectMapper.convertValue(recordsNode, new TypeReference<List<DicionarioDTO>>() {});

        } catch (JsonProcessingException | WebClientResponseException e) {
            throw new RuntimeException(e);
        }
    }

    public List<LinhaDTO> getLinhasAPIBH() {
        try {
            String json = webClient.get()
                    .uri("https://ckan.pbh.gov.br/api/3/action/datastore_search?resource_id=150bddd0-9a2c-4731-ade9-54aa56717fb6&limit=3000")
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            JsonNode rootNode = objectMapper.readTree(json);
            JsonNode recordsNode = rootNode.path("result").path("records");

            return objectMapper.convertValue(recordsNode, new TypeReference<List<LinhaDTO>>() {});

        } catch (JsonProcessingException | WebClientResponseException e) {
            throw new RuntimeException(e);
        }
    }

    public List<CoordenadaDTO> getOnibusCoordenadaBH() {
        List<String> responses = fetchCoordenadasDirectly();

        if (responses == null || responses.size() < 2) {
            throw new IllegalStateException("Não foi possível obter resposta de um ou mais endpoints.");
        }

        return processAndReturnCoordenadas(responses);
    }

    private List<String> fetchCoordenadasDirectly() {
        Mono<String> monoParamD = webClient.get()
                .uri("https://temporeal.pbh.gov.br/?param=D")
                .retrieve()
                .bodyToMono(String.class)
                .onErrorResume(e -> {
                    logger.error("Erro ao buscar coordenadas D", e);
                    return Mono.just("[]");
                });

        Mono<String> monoParamSD = webClient.get()
                .uri("https://temporeal.pbh.gov.br/?param=SD")
                .retrieve()
                .bodyToMono(String.class)
                .onErrorResume(e -> {
                    logger.error("Erro ao buscar coordenadas SD", e);
                    return Mono.just("[]");
                });

        return Flux.mergeSequential(monoParamD, monoParamSD).collectList().block();
    }

    private List<CoordenadaDTO> processAndReturnCoordenadas(List<String> responses) {
        String jsonD = responses.get(0);
        String jsonSD = responses.get(1);
        List<CoordenadaDTO> todasCoordenadas = new ArrayList<>();

        try {
            TypeReference<List<CoordenadaDTO>> typeRef = new TypeReference<>() {};
            
            List<CoordenadaDTO> listD = objectMapper.readValue(jsonD, typeRef);
            List<CoordenadaDTO> listSD = objectMapper.readValue(jsonSD, typeRef);

            if(listD != null) todasCoordenadas.addAll(listD);
            if(listSD != null) todasCoordenadas.addAll(listSD);

            return todasCoordenadas;

        } catch (JsonProcessingException e) {
            throw new RuntimeException("Erro ao processar o JSON das coordenadas.", e);
        }
    }
}