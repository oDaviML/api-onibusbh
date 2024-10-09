package com.dmware.api_onibusbh.services;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dmware.api_onibusbh.config.WebClientConfig;
import com.dmware.api_onibusbh.dto.LinhaDTO;
import com.dmware.api_onibusbh.entities.LinhaEntity;
import com.dmware.api_onibusbh.repositories.LinhasRepository;
import com.dmware.api_onibusbh.scheduler.CoordenadasScheduler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class LinhasService {

      @Autowired
      private WebClientConfig webClientConfig;
      @Autowired
      private LinhasRepository linhasRepository;
      @Autowired
      private ModelMapper modelMapper;

      private static final Logger logger = LoggerFactory.getLogger(LinhasService.class);
      ObjectMapper objectMapper = new ObjectMapper();

      public List<LinhaDTO> fetchLinhas() {
            // Consome API PBH para buscar linhas
            String json = webClientConfig.webClient().get()
                        .uri("https://ckan.pbh.gov.br/api/3/action/datastore_search?resource_id=150bddd0-9a2c-4731-ade9-54aa56717fb6&limit=3000")
                        .retrieve()
                        .bodyToMono(String.class)
                        .block();

            try {
                  // Lê o json e navega até o campo "result" -> "records"
                  JsonNode rootNode = objectMapper.readTree(json);
                  JsonNode recordsNode = rootNode.path("result").path("records");

                  // Deserializa o JsonNode em uma lista de objetos LinhaDTO
                  List<LinhaDTO> linhasNovas = objectMapper.readValue(recordsNode.toString(),
                              new TypeReference<List<LinhaDTO>>() {
                              });

                  // Converte LinhaDTO para LinhaEntity
                  Type listType = new TypeToken<List<LinhaEntity>>() {
                  }.getType();

                  List<LinhaEntity> listaLinhasNovas = modelMapper.map(linhasNovas, listType);
                  logger.info("Linhas encontradas: " + listaLinhasNovas.size());

                  // Salva as novas linhas
                  salvaLinhasBanco(listaLinhasNovas);

                  return linhasNovas;

            } catch (JsonProcessingException e) {
                  e.printStackTrace();
                  return null;
            }
      }

      private void salvaLinhasBanco(List<LinhaEntity> listaLinhasNovas) {

            // Busca as linhas já existentes no banco
            List<LinhaEntity> linhasExistentes = linhasRepository.findAll();
            logger.info("Linhas existentes no banco: " + linhasExistentes.size());

            // Cria uma lista para armazenar os dados que precisam ser atualizados
            List<LinhaEntity> linhasParaSalvar = new ArrayList<>();

            // Mapeia as linhas existentes para um Map por idLinha, para fácil comparação
            Map<Long, LinhaEntity> linhasExistentesMap = linhasExistentes.stream()
                        .collect(Collectors.toMap(LinhaEntity::getIdLinha, linha -> linha));

            // Verifica as linhas novas
            for (LinhaEntity novaLinha : listaLinhasNovas) {
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
                  linhasRepository.saveAll(linhasParaSalvar);
            }
      }

}
