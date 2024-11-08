package com.dmware.api_onibusbh.services;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.stereotype.Service;

import com.dmware.api_onibusbh.config.WebClientConfig;
import com.dmware.api_onibusbh.dto.CoordenadaDTO;
import com.dmware.api_onibusbh.dto.DicionarioDTO;
import com.dmware.api_onibusbh.dto.LinhaDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.core.publisher.Flux;

@Service
public class APIService {

    @Autowired
    private WebClientConfig webClientConfig;

    ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger logger = LoggerFactory.getLogger(APIService.class);

    private static final String BASE_PATH = "src/data/coordenadas";
    private static final String FILE_NAME = "coordenadas.json";
    private static final String TEMP_FILE_NAME = "coordenadas_temp.json";

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

        Path directory = Paths.get(BASE_PATH);
        Path tempFile = directory.resolve(TEMP_FILE_NAME);
        Path targetFile = directory.resolve(FILE_NAME);

        try {
            // Cria diretório se não existir
            Files.createDirectories(directory);

            // Busca dados da API e salva em arquivo temporário
            Flux<DataBuffer> dataBufferFlux = webClientConfig.webClient().get()
                    .uri("https://temporeal.pbh.gov.br/?param=D")
                    .retrieve()
                    .bodyToFlux(DataBuffer.class)
                    .doOnNext(DataBufferUtils::release);

            // Salva em arquivo temporário
            try (FileChannel fileChannel = FileChannel.open(tempFile,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.WRITE,
                    StandardOpenOption.TRUNCATE_EXISTING)) {

                DataBufferUtils.write(dataBufferFlux, fileChannel)
                        .doOnError(error -> {
                            logger.error("Erro durante a escrita do arquivo temporário", error);
                            try {
                                Files.deleteIfExists(tempFile);
                            } catch (IOException e) {
                                logger.error("Erro ao tentar deletar arquivo temporário", e);
                            }
                        })
                        .blockLast();
            }

            // Valida se o arquivo temporário contém JSON válido e não está vazio
            if (!isValidJsonFile(tempFile)) {
                throw new IllegalStateException("Arquivo temporário contém JSON inválido ou vazio");
            }

            // Move o arquivo temporário para o arquivo final de forma atômica
            Files.move(tempFile, targetFile, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            logger.info("Sincronização de coordenadas concluída com sucesso");

        } catch (IOException | IllegalStateException e) {
            logger.error("Erro durante a sincronização de coordenadas", e);
            try {
                Files.deleteIfExists(tempFile);
            } catch (IOException ioE) {
                logger.error("Erro ao tentar deletar arquivo temporário após falha", ioE);
            }
            throw new RuntimeException("Erro ao sincronizar coordenadas", e);
        }
    }

    private boolean isValidJsonFile(Path file) {
        try {
            String content = Files.readString(file);
            if (content.trim().isEmpty()) {
                return false;
            }

            // Tenta ler como lista de CoordenadaDTO
            List<CoordenadaDTO> coordenadas = objectMapper.readValue(
                    content,
                    new TypeReference<List<CoordenadaDTO>>() {
                    });

            return !coordenadas.isEmpty();

        } catch (IOException e) {
            logger.error("Erro ao validar arquivo JSON", e);
            return false;
        }
    }

}
