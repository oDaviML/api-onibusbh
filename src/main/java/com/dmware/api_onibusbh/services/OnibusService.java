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
import java.time.LocalDateTime;
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

    @Value("${api.onibus.coordenadas.ttl-minutes:20}")
    private int ttlMinutes;

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

    public void salvaCoordenadas() {
        logger.info("Iniciando o salvamento das coordenadas...");
        List<LinhaEntity> linhasExistentes;
        try {
            // Busca as entidades diretamente para evitar conversão DTO -> Entity
            linhasExistentes = linhasRepository.findAll();
            if (linhasExistentes.isEmpty()) {
                logger.warn("Nenhuma linha encontrada no banco de dados. Coordenadas não serão salvas.");
                return;
            }
        } catch (Exception e) {
            logger.error("Erro ao buscar linhas do banco de dados.", e);
            return;
        }

        List<CoordenadaDTO> todasCoordenadasNovas = fetchCoordenadas();
        
        // Agrupa as coordenadas novas mais recentes por número de linha
        Map<Integer, List<CoordenadaDTO>> mapaNovasCoordenadasPorLinha;
        if (todasCoordenadasNovas.isEmpty()) {
            logger.info("Nenhuma coordenada nova encontrada. Executando apenas limpeza por TTL.");
            mapaNovasCoordenadasPorLinha = Collections.emptyMap();
        } else {
             mapaNovasCoordenadasPorLinha = todasCoordenadasNovas.stream()
                    .collect(Collectors.toMap(
                            CoordenadaDTO::getNumeroVeiculo,
                            Function.identity(),
                            BinaryOperator.maxBy(Comparator.comparing(CoordenadaDTO::getHorario))
                    ))
                    .values().stream()
                    .filter(c -> c.getNumeroLinha() != null)
                    .collect(Collectors.groupingBy(CoordenadaDTO::getNumeroLinha));
        }

        LocalDateTime dataLimite = LocalDateTime.now().minusMinutes(ttlMinutes);
        List<LinhaEntity> linhasParaSalvar = new ArrayList<>();

        for (LinhaEntity linha : linhasExistentes) {
            boolean houveAlteracao = false;
            
            // Mapa de veículos atuais da linha (NumeroVeiculo -> DTO)
            Map<String, CoordenadaDTO> veiculosMap = new HashMap<>();
            if (linha.getCoordenadas() != null) {
                for (CoordenadaDTO c : linha.getCoordenadas()) {
                    if (c.getNumeroVeiculo() != null) {
                        veiculosMap.put(c.getNumeroVeiculo(), c);
                    }
                }
            }

            // Merge com as novas coordenadas (se houver para esta linha)
            List<CoordenadaDTO> novasDaLinha = mapaNovasCoordenadasPorLinha.get(linha.getNumeroLinha());
            if (novasDaLinha != null) {
                for (CoordenadaDTO nova : novasDaLinha) {
                    veiculosMap.put(nova.getNumeroVeiculo(), nova);
                    houveAlteracao = true;
                }
            }

            // Filtragem por TTL (remove veículos antigos)
            int tamanhoAntes = veiculosMap.size();
            List<CoordenadaDTO> veiculosAtualizados = veiculosMap.values().stream()
                    .filter(c -> c.getHorario() != null && c.getHorario().isAfter(dataLimite))
                    .collect(Collectors.toList());
            
            if (tamanhoAntes != veiculosAtualizados.size()) houveAlteracao = true;

            if (houveAlteracao) {
                linha.setCoordenadas(veiculosAtualizados);

                List<Character> sentidos = veiculosAtualizados.stream()
                        .map(CoordenadaDTO::getSentido)
                        .filter(sentido -> sentido != null && sentido != '0')
                        .distinct()
                        .toList();

                linha.setSentidoIsUnique(!sentidos.isEmpty() && sentidos.size() == 1);
                
                linhasParaSalvar.add(linha);
            }
        }

        if (!linhasParaSalvar.isEmpty()) {
            linhasRepository.saveAll(linhasParaSalvar);
            logger.info("Sincronização concluída. Linhas atualizadas: {}", linhasParaSalvar.size());
        } else {
            logger.info("Nenhuma alteração necessária nas linhas.");
        }
    }

    public List<CoordenadaDTO> fetchCoordenadas() {
        try {
            Path file = Paths.get(BASE_PATH, FILE_NAME);
            if (!Files.exists(file)) {
                logger.warn("Arquivo de coordenadas não encontrado");
                throw new CoordenadasNotFoundException();
            }

            return objectMapper.readValue(
                    file.toFile(),
                    new TypeReference<>() {
                    });

        } catch (CoordenadasNotFoundException | IOException e) {
            logger.error("Erro ao ler arquivo de coordenadas");
            throw new RuntimeException(e);
        }
    }
}
