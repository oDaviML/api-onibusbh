package com.dmware.api_onibusbh.services;

import com.dmware.api_onibusbh.dto.LinhaDTO;
import com.dmware.api_onibusbh.entities.LinhaEntity;
import com.dmware.api_onibusbh.enuns.Tipo;
import com.dmware.api_onibusbh.exceptions.LinhaNotFoundException;
import com.dmware.api_onibusbh.exceptions.LinhasNotFoundException;
import com.dmware.api_onibusbh.repositories.LinhasRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static net.logstash.logback.argument.StructuredArguments.kv;

@Service
public class LinhasService {

    private static final Logger logger = LoggerFactory.getLogger(LinhasService.class);
    private final LinhasRepository linhasRepository;
    private final ModelMapper modelMapper;
    private final APIService apiService;

    public LinhasService(LinhasRepository linhasRepository, ModelMapper modelMapper, APIService apiService) {
        this.linhasRepository = linhasRepository;
        this.modelMapper = modelMapper;
        this.apiService = apiService;
    }

    public List<LinhaDTO> fetchLinhas() {
        List<LinhaEntity> linhaEntities = linhasRepository.findAll();

        if (linhaEntities.isEmpty()) {
            throw new LinhasNotFoundException();
        }
        return modelMapper.map(linhaEntities, new TypeToken<List<LinhaDTO>>() {
        }.getType());
    }

    public List<LinhaDTO> fetchLinhasCoordenadasNotEmpty() {
        List<LinhaEntity> linhaEntities = linhasRepository.findLinhasWithCoordenadas();

        if (linhaEntities.isEmpty()) {
            throw new LinhasNotFoundException();
        }

        // Filtra apenas as linhas que possuem coordenadas válidas
        List<LinhaEntity> linhasFiltradas = linhaEntities.stream()
                .filter(linha -> linha.getCoordenadas() != null &&
                        linha.getCoordenadas().stream()
                                .anyMatch(coord -> coord.getSentido() != null &&
                                        (coord.getSentido() == '1' || coord.getSentido() == '2')))
                .toList();

        if (linhasFiltradas.isEmpty()) {
            throw new LinhasNotFoundException();
        }

        return modelMapper.map(linhasFiltradas, new TypeToken<List<LinhaDTO>>() {
        }.getType());
    }


    public LinhaDTO listarLinhaPorNumero(Integer numeroLinha) {
        Optional<LinhaEntity> linhaEntity = linhasRepository.findByNumeroLinha(numeroLinha);

        if (linhaEntity.isEmpty()) {
            throw new LinhaNotFoundException();
        }
        return modelMapper.map(linhaEntity.get(), LinhaDTO.class);
    }

    public void salvaLinhasNormais() {
        logger.info("Iniciando sincronização de linhas normais.");
        List<LinhaDTO> linhasDaAPI = apiService.getLinhasAPIBH();
        List<LinhaEntity> linhasExistentes = linhasRepository.findAll();
        logger.info("Estado inicial do banco", kv("total_existente", linhasExistentes.size()));

        Map<Integer, LinhaEntity> linhasExistentesMap = linhasExistentes.stream()
                .collect(Collectors.toMap(LinhaEntity::getIdLinha, linha -> linha));

        List<LinhaEntity> linhasParaSalvar = linhasDaAPI.stream()
                .map(l -> {
                    l.setTipo(Tipo.NORMAL);
                    return modelMapper.map(l, LinhaEntity.class);
                })
                .filter(linhaNova -> {
                    LinhaEntity linhaExistente = linhasExistentesMap.get(linhaNova.getIdLinha());

                    return linhaExistente == null || !linhaNova.getLinha().equals(linhaExistente.getLinha());
                })
                .toList();

        if (!linhasParaSalvar.isEmpty()) {
            linhasRepository.saveAll(linhasParaSalvar);
            logger.info("Linhas normais sincronizadas com sucesso", kv("total_sincronizado", linhasParaSalvar.size()));
        } else {
            logger.info("Nenhuma linha nova ou atualizada encontrada para sincronização.");
        }
    }

}
