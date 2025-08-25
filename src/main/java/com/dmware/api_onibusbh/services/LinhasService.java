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
        List<LinhaDTO> linhaDTOS = modelMapper.map(linhaEntities, new TypeToken<List<LinhaDTO>>() {
        }.getType());
        return linhaDTOS;
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

        if (!linhaEntity.isPresent()) {
            throw new LinhaNotFoundException();
        }
        LinhaDTO linhaDTO = modelMapper.map(linhaEntity.get(), LinhaDTO.class);
        return linhaDTO;
    }

    public void salvaLinhasBanco() {
        logger.info("Iniciando sincronização de linhas");
        List<LinhaDTO> listaLinhasNovas = apiService.getLinhasAPIBH();

        // Busca as linhas já existentes no banco
        List<LinhaEntity> linhasExistentes = linhasRepository.findAll();
        logger.info("Linhas existentes no banco: " + linhasExistentes.size());

        // Cria uma lista para armazenar os dados que precisam ser atualizados
        List<LinhaDTO> linhasParaSalvar = new ArrayList<>();

        // Mapeia as linhas existentes para um Map por idLinha, para fácil comparação
        Map<Integer, LinhaEntity> linhasExistentesMap = linhasExistentes.stream()
                .collect(Collectors.toMap(LinhaEntity::getIdLinha, linha -> linha));

        // Verifica as linhas novas
        for (LinhaDTO novaLinha : listaLinhasNovas) {
            LinhaEntity linhaExistente = linhasExistentesMap.get(novaLinha.getIdLinha());

            if (linhaExistente == null) {
                // Se a linha não existir no banco, é um novo registro
                logger.info("Nova linha encontrada: " + novaLinha.getLinha());
                linhasParaSalvar.add(novaLinha);
            } else if (!novaLinha.getLinha().equals(linhaExistente.getLinha())) {
                // Se os dados forem diferentes, a linha foi atualizada
                logger.info("Linha atualizada: " + novaLinha.getLinha());
                linhasParaSalvar.add(novaLinha);
            }
        }

        // Salva apenas as linhas que foram novas ou atualizadas
        if (!linhasParaSalvar.isEmpty()) {
            logger.info("Linhas novas ou atualizadas: " + linhasParaSalvar.size());
            linhasRepository.saveAll(modelMapper.map(linhasParaSalvar, new TypeToken<List<LinhaEntity>>() {
            }.getType()));
        }
    }

}
