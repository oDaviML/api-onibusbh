package com.dmware.api_onibusbh.services;

import com.dmware.api_onibusbh.dto.CoordenadaDTO;
import com.dmware.api_onibusbh.dto.OnibusDTO;
import com.dmware.api_onibusbh.entities.LinhaEntity;
import com.dmware.api_onibusbh.exceptions.CoordenadasNotFoundException;
import com.dmware.api_onibusbh.exceptions.LinhaNotFoundException;
import com.dmware.api_onibusbh.repositories.LinhasRepository;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;

import static net.logstash.logback.argument.StructuredArguments.kv;

@Service
public class OnibusService {

    private static final Logger logger = LoggerFactory.getLogger(OnibusService.class);

    @Value("${api.onibus.coordenadas.ttl-minutes:20}")
    private int ttlMinutes;

    private final LinhasRepository linhasRepository;
    private final ModelMapper modelMapper;

    public OnibusService(LinhasRepository linhasRepository, ModelMapper modelMapper) {
        this.linhasRepository = linhasRepository;
        this.modelMapper = modelMapper;
    }


    @Cacheable(value = "onibus")
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

    @Cacheable(value = "onibusPorLinha", key = "#numeroLinha")
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

    @CacheEvict(value = {"onibus", "onibusPorLinha"}, allEntries = true)
    public void salvaCoordenadas(List<CoordenadaDTO> todasCoordenadasNovas) {
        logger.info("Iniciando o salvamento das coordenadas...");
        List<LinhaEntity> linhasExistentes;
        try {
            linhasExistentes = linhasRepository.findAll();
            if (linhasExistentes.isEmpty()) {
                logger.warn("Nenhuma linha encontrada no banco de dados. Coordenadas não serão salvas.");
                return;
            }
        } catch (Exception e) {
            logger.error("Erro ao buscar linhas do banco de dados.", e);
            return;
        }

        Map<Integer, List<CoordenadaDTO>> mapaNovasCoordenadasPorLinha;
        if (todasCoordenadasNovas == null || todasCoordenadasNovas.isEmpty()) {
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

        LocalDateTime dataLimite = LocalDateTime.now(ZoneId.of("America/Sao_Paulo")).minusMinutes(ttlMinutes);
        List<LinhaEntity> linhasParaSalvar = new ArrayList<>();

        for (LinhaEntity linha : linhasExistentes) {
            boolean houveAlteracao = false;

            Map<String, CoordenadaDTO> veiculosMap = new HashMap<>();
            if (linha.getCoordenadas() != null) {
                for (CoordenadaDTO c : linha.getCoordenadas()) {
                    if (c.getNumeroVeiculo() != null) {
                        veiculosMap.put(c.getNumeroVeiculo(), c);
                    }
                }
            }

            List<CoordenadaDTO> novasDaLinha = mapaNovasCoordenadasPorLinha.get(linha.getNumeroLinha());

            if (novasDaLinha != null) {
                for (CoordenadaDTO nova : novasDaLinha) {
                    veiculosMap.put(nova.getNumeroVeiculo(), nova);
                    houveAlteracao = true;
                }
            }

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
            logger.info("Processo de atualização de coordenadas finalizado. Total de linhas com veículos atualizados: {}", linhasParaSalvar.size(), kv("linhas_atualizadas", linhasParaSalvar.size()));
        } else {
            logger.info("Nenhuma alteração necessária nas linhas.");
        }
    }
}
