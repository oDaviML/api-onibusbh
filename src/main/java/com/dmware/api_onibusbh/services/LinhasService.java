package com.dmware.api_onibusbh.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dmware.api_onibusbh.dto.LinhaDTO;
import com.dmware.api_onibusbh.entities.LinhaEntity;
import com.dmware.api_onibusbh.exceptions.LinhaNotFoundException;
import com.dmware.api_onibusbh.exceptions.LinhasNotFoundException;
import com.dmware.api_onibusbh.repositories.LinhasRepository;

@Service
public class LinhasService {

      @Autowired
      private LinhasRepository linhasRepository;
      @Autowired
      private ModelMapper modelMapper;
      @Autowired
      private APIService apiService;

      private static final Logger logger = LoggerFactory.getLogger(LinhasService.class);

      public List<LinhaDTO> fetchLinhas() {
            List<LinhaEntity> linhaEntities = linhasRepository.findAll();

            if (linhaEntities.isEmpty()) {
                  throw new LinhasNotFoundException();
            }
            List<LinhaDTO> linhaDTOS = modelMapper.map(linhaEntities, new TypeToken<List<LinhaDTO>>() {
            }.getType());
            return linhaDTOS;
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
