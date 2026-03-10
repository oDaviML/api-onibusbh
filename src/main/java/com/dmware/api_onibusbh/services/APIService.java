package com.dmware.api_onibusbh.services;

import com.dmware.api_onibusbh.dto.CoordenadaDTO;
import com.dmware.api_onibusbh.dto.DicionarioDTO;
import com.dmware.api_onibusbh.dto.LinhaDTO;
import com.dmware.api_onibusbh.exceptions.CoordenadasApiIntegrationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

import static net.logstash.logback.argument.StructuredArguments.kv;

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
        String endpoint = "/dicionario";
        try {
            String json = webClient.get()
                    .uri(endpoint)
                    .retrieve()
                    .bodyToMono(String.class)
                    .onErrorResume(e -> {
                        logger.error("Erro ao buscar dicionário da API BH", kv("endpoint", endpoint), kv("error", e.getMessage()));
                        return Mono.error(new CoordenadasApiIntegrationException("Falha ao buscar dicionário", endpoint, e.getClass().getSimpleName(), e));
                    })
                    .block();

            JsonNode rootNode = objectMapper.readTree(json);
            JsonNode recordsNode = rootNode.path("result").path("records");

            return objectMapper.convertValue(recordsNode, new TypeReference<List<DicionarioDTO>>() {
            });

        } catch (JsonProcessingException e) {
            logger.error("Erro ao processar JSON do dicionário", kv("endpoint", endpoint), kv("error", e.getMessage()));
            throw new CoordenadasApiIntegrationException("Erro ao processar resposta do dicionário", endpoint, "JsonProcessingException", e);
        }
    }

    public List<LinhaDTO> getLinhasAPIBH() {
        String endpoint = "/linhas";
        try {
            String json = webClient.get()
                    .uri(endpoint)
                    .retrieve()
                    .bodyToMono(String.class)
                    .onErrorResume(e -> {
                        logger.error("Erro ao buscar linhas da API BH", kv("endpoint", endpoint), kv("error", e.getMessage()));
                        return Mono.error(new CoordenadasApiIntegrationException("Falha ao buscar linhas", endpoint, e.getClass().getSimpleName(), e));
                    })
                    .block();

            JsonNode rootNode = objectMapper.readTree(json);
            JsonNode recordsNode = rootNode.path("result").path("records");

            return objectMapper.convertValue(recordsNode, new TypeReference<List<LinhaDTO>>() {
            });

        } catch (JsonProcessingException e) {
            logger.error("Erro ao processar JSON das linhas", kv("endpoint", endpoint), kv("error", e.getMessage()));
            throw new CoordenadasApiIntegrationException("Erro ao processar resposta das linhas", endpoint, "JsonProcessingException", e);
        }
    }

    public List<CoordenadaDTO> getOnibusCoordenadaBH() {
        List<String> responses = fetchCoordenadasDirectly();

        if (responses == null || responses.size() < 2) {
            throw new CoordenadasApiIntegrationException("Resposta incompleta das APIs de coordenadas", "/coordenadas", "IncompleteResponse", null);
        }

        String jsonD = responses.get(0);
        String jsonSD = responses.get(1);

        if ("[]".equals(jsonD) && "[]".equals(jsonSD)) {
            logger.warn("Ambas as APIs de coordenadas retornaram vazio");
            throw new CoordenadasApiIntegrationException("APIs de coordenadas retornaram dados vazios", "/coordenadas", "EmptyResponse", null);
        }

        return processAndReturnCoordenadas(responses);
    }

    private List<String> fetchCoordenadasDirectly() {
        Mono<String> monoParamD = webClient.get()
                .uri("/coordenadas?param=D")
                .retrieve()
                .bodyToMono(String.class)
                .onErrorResume(e -> {
                    logger.error("Erro ao buscar coordenadas D", kv("param", "D"), kv("error", e.getMessage()));
                    return Mono.error(new CoordenadasApiIntegrationException("Falha ao buscar coordenadas D", "/coordenadas?param=D", e.getClass().getSimpleName(), e));
                });

        Mono<String> monoParamSD = webClient.get()
                .uri("/coordenadas?param=SD")
                .retrieve()
                .bodyToMono(String.class)
                .onErrorResume(e -> {
                    logger.error("Erro ao buscar coordenadas SD", kv("param", "SD"), kv("error", e.getMessage()));
                    return Mono.error(new CoordenadasApiIntegrationException("Falha ao buscar coordenadas SD", "/coordenadas?param=SD", e.getClass().getSimpleName(), e));
                });

        return Flux.mergeSequential(monoParamD, monoParamSD).collectList().block();
    }

    private List<CoordenadaDTO> processAndReturnCoordenadas(List<String> responses) {
        String jsonD = responses.get(0);
        String jsonSD = responses.get(1);
        List<CoordenadaDTO> todasCoordenadas = new ArrayList<>();

        try {
            TypeReference<List<CoordenadaDTO>> typeRef = new TypeReference<>() {
            };

            List<CoordenadaDTO> listD = objectMapper.readValue(jsonD, typeRef);
            List<CoordenadaDTO> listSD = objectMapper.readValue(jsonSD, typeRef);

            if (listD != null) todasCoordenadas.addAll(listD);
            if (listSD != null) todasCoordenadas.addAll(listSD);

            return todasCoordenadas;

        } catch (JsonProcessingException e) {
            throw new CoordenadasApiIntegrationException("Erro ao processar JSON das coordenadas", "/coordenadas", "JsonProcessingException", e);
        }
    }
}
