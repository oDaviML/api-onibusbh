package com.dmware.api_onibusbh.services;

import com.dmware.api_onibusbh.config.WebClientConfig;
import com.dmware.api_onibusbh.dto.DicionarioDTO;
import com.dmware.api_onibusbh.dto.LinhaDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class APIService {

    @Autowired
    private WebClientConfig webClientConfig;

    ObjectMapper objectMapper = new ObjectMapper();

    private static final Logger logger = LoggerFactory.getLogger(APIService.class);

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

        } catch (JsonProcessingException e) {
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
            List<LinhaDTO> linhasNovas = objectMapper.readValue(recordsNode.toString(),
                    new TypeReference<List<LinhaDTO>>() {
                    });

            return linhasNovas;

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public void getOnibusCoordenadaBH() {
        logger.info("Iniciando sincronização de coordenadas");

        // Consome API PBH para buscar coordenadas e realizar o download do arquivo
        // .json
        Flux<DataBuffer> dataBufferFlux = webClientConfig.webClient().get()
                .uri("https://temporeal.pbh.gov.br/?param=D")
                .retrieve()
                .bodyToFlux(DataBuffer.class);

        Path directory = Paths.get("src/data/coordenadas");

        // Verifica se o diretório de destino existe, se não, cria-o
        if (Files.notExists(directory)) {
            try {
                Files.createDirectories(directory);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        Path path = directory.resolve("coordenadas.json");

        try (FileOutputStream outputStream = new FileOutputStream(path.toFile())) {
            // Converte os dados do buffer para bytes e escreve no arquivo
            DataBufferUtils.write(dataBufferFlux, outputStream.getChannel())
                    .doOnComplete(() -> logger.info("Sincronização de coordenadas concluída"))
                    .blockLast(); // Garantir que o processo seja bloqueante e termine antes de prosseguir
        } catch (IOException e) {
            logger.error("Erro ao tentar sincronizar as coordenadas", e);
        }
    }

}