package com.dmware.api_onibusbh.services;

import com.dmware.api_onibusbh.config.WebClientConfig;
import com.dmware.api_onibusbh.dto.CoordenadaDTO;
import com.dmware.api_onibusbh.dto.LinhaDTO;
import com.dmware.api_onibusbh.dto.OnibusDTO;
import com.dmware.api_onibusbh.entities.LinhaEntity;
import com.dmware.api_onibusbh.exceptions.CoordenadasNotFoundException;
import com.dmware.api_onibusbh.exceptions.LinhaNotFoundException;
import com.dmware.api_onibusbh.exceptions.LinhasNotFoundException;
import com.dmware.api_onibusbh.repositories.LinhasRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class OnibusService {

    private static final Logger logger = LoggerFactory.getLogger(OnibusService.class);

    @Value("${BASE_PATH}")
    private String BASE_PATH;
    @Value("${FILE_NAME}")
    private String FILE_NAME;

    private final LinhasRepository linhasRepository;
    private final ModelMapper modelMapper;
    private final ObjectMapper objectMapper;

    public OnibusService(LinhasService linhasService, WebClientConfig webClientConfig, LinhasRepository linhasRepository, ModelMapper modelMapper, ObjectMapper objectMapper) {
        this.linhasRepository = linhasRepository;
        this.modelMapper = modelMapper;
        this.objectMapper = objectMapper;
    }


    public List<OnibusDTO> listarTodosOnibus() {
        List<LinhaEntity> linhas = linhasRepository.findAll();

        return linhas.stream()
                .filter(linha -> linha.getCoordenadas() != null && !linha.getCoordenadas().isEmpty())
                .map(linha -> {
                    OnibusDTO onibusDTO = modelMapper.map(linha, OnibusDTO.class);
                    List<CoordenadaDTO> coordenadas = linha.getCoordenadas().stream()
                            .filter(coordenadaDTO -> {
                                Character sentido = coordenadaDTO.getSentido();
                                return sentido != null && (sentido == '1' || sentido == '2');
                            })
                            .toList();
                    onibusDTO.setCoordenadas(coordenadas);
                    return onibusDTO;
                }).collect(Collectors.toList());

        return onibus;
    }

    public List<CoordenadaDTO> listarPorNumeroLinha(Integer numeroLinha, Optional<Integer> sentido) {
        Optional<LinhaEntity> linha = linhasRepository.findByNumeroLinha(numeroLinha);

        if (linha.isEmpty()) {
            throw new LinhaNotFoundException();
        }

        List<CoordenadaDTO> coordenadas = linha.get().getCoordenadas();

        if (coordenadas == null || coordenadas.isEmpty()) {
            throw new CoordenadasNotFoundException();
        }

        if (sentido.isPresent()) {
            coordenadas = coordenadas.stream()
                    .filter(coordenada -> coordenada.getSentido() != null &&
                            coordenada.getSentido().equals(Character.forDigit(sentido.get(), 10)))
                    .collect(Collectors.toList());
        }

        return coordenadas;
    }

    private void salvaCoordenadas() {
        List<LinhaDTO> linhas;
        try {
            linhas = linhasService.fetchLinhas();
        } catch (LinhasNotFoundException e) {
            logger.warn("Nenhuma linha encontrada no banco de dados. Coordenadas não serão salvas neste ciclo.");
            return;
        }

        List<CoordenadaDTO> coordenadas = fetchCoordenadas();

        // Filtra as coordenadas pelo número do veículo, deixando apenas as que forem
        // mais recentes em relação ao horário
        List<CoordenadaDTO> coordenadasFiltradas = coordenadas.stream()
                .collect(Collectors.toMap(
                        CoordenadaDTO::getNumeroVeiculo,
                        Function.identity(),
                        BinaryOperator.maxBy(Comparator.comparing(CoordenadaDTO::getHorario))))
                .values().stream().toList();

        HashMap<Integer, List<CoordenadaDTO>> coordenadasPorLinha = new HashMap<>();

        // Mapeia as linhas com suas respectivas coordenadas
        linhas.forEach(linha -> {
            coordenadasPorLinha.put(linha.getNumeroLinha(), coordenadasFiltradas.stream()
                    .filter(coord -> Objects.equals(coord.getNumeroLinha(), linha.getNumeroLinha()))
                    .collect(Collectors.toList()));
        });

        // Cria uma lista de LinhaEntity com suas respectivas coordenadas e em seguida
        // salva no banco
        List<LinhaEntity> linhasEntities = linhas.stream()
                .filter(linha -> !coordenadasPorLinha.get(linha.getNumeroLinha()).isEmpty())
                .map(linha -> {
                    LinhaEntity linhaEntity = modelMapper.map(linha, LinhaEntity.class);
                    List<CoordenadaDTO> coordenadasLinha = coordenadasPorLinha.get(linha.getNumeroLinha());
                    linhaEntity.setCoordenadas(coordenadasLinha);

                    List<Character> sentidos = coordenadasLinha.stream()
                            .map(CoordenadaDTO::getSentido)
                            .filter(sentido -> sentido != null && sentido != '0')
                            .toList();
                    linhaEntity.setSentidoIsUnique(!sentidos.isEmpty() &&
                            sentidos.stream().distinct().count() == 1);

                    return linhaEntity;
                }).collect(Collectors.toList());

        linhasRepository.saveAll(linhasEntities);

    }

    private List<CoordenadaDTO> fetchCoordenadas() {
        try {
            Path file = Paths.get(BASE_PATH, FILE_NAME);
            if (!Files.exists(file)) {
                logger.warn("Arquivo de coordenadas não encontrado");
                throw new CoordenadasNotFoundException();
            }

            List<CoordenadaDTO> coordenadas = objectMapper.readValue(
                    file.toFile(),
                    new TypeReference<List<CoordenadaDTO>>() {
                    });

            return coordenadas;

        } catch (CoordenadasNotFoundException | IOException e) {
            logger.error("Erro ao ler arquivo de coordenadas");
            throw new RuntimeException(e);
        }
    }
}
