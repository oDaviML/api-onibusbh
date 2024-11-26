package com.dmware.api_onibusbh.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.dmware.api_onibusbh.exceptions.DicionarioNotFoundException;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dmware.api_onibusbh.dto.DicionarioDTO;
import com.dmware.api_onibusbh.entities.DicionarioEntity;
import com.dmware.api_onibusbh.repositories.DicionarioRepository;

@Service
public class DicionarioService {

      @Autowired
      private DicionarioRepository dicionarioRepository;
      @Autowired
      private ModelMapper modelMapper;
      @Autowired
      private APIService apiService;

      private static final Logger logger = LoggerFactory.getLogger(LinhasService.class);

      // TODO: Alterar mapeando pois está exibindo as variáveis com os nomes do json retornado

      public List<DicionarioDTO> fetchDicionarios() {
            List<DicionarioEntity> dicionarioEntityList = dicionarioRepository.findAll();
            List<DicionarioDTO> dicionarioDTOList = modelMapper.map(dicionarioEntityList,
                        new TypeToken<List<DicionarioDTO>>() {
                        }.getType());
            if (dicionarioDTOList.isEmpty()) {
                  throw new DicionarioNotFoundException();
            }
            return dicionarioDTOList;
      }

      public void salvarDicionarioBanco() {
            logger.info("Iniciando sincronização dicionário");

            List<DicionarioDTO> listaDados = apiService.getDicionarioAPIBH();
            List<DicionarioEntity> dadosExistentes = dicionarioRepository.findAll();
            logger.info("Dados existentes no banco de dados: " + dadosExistentes.size());

            // Mapeia os dados da api para facilitar a comparação
            Map<String, DicionarioEntity> dicionarioMap = dadosExistentes.stream()
                        .collect(Collectors.toMap(DicionarioEntity::getIdDicionario,
                                    dicionarioEntity -> dicionarioEntity));

            List<DicionarioDTO> dadosParaSalvar = new ArrayList<>();

            // Realiza a comparação, se for diferente, insere no banco de dados
            for (DicionarioDTO dicionarioDTO : listaDados) {
                  DicionarioEntity dicionarioComparacao = dicionarioMap.get(dicionarioDTO.getId());

                  if (dicionarioComparacao == null) {
                        logger.info("Novo dado encontrado: " + dicionarioDTO.getNomeArquivo());
                        dadosParaSalvar.add(dicionarioDTO);
                  } else if (!dicionarioDTO.getNomeArquivo().equals(dicionarioComparacao.getNomeArquivo())) {
                        logger.info("Novo dado atualizado: " + dicionarioDTO.getNomeArquivo());
                        dadosParaSalvar.add(dicionarioDTO);
                  }
            }

            // Salva apenas os dados que forem novos ou atualizados
            if (!dadosParaSalvar.isEmpty()) {
                  logger.info("Dicionário novo ou atualizados: " + dadosParaSalvar.size());
                  dicionarioRepository
                              .saveAll(modelMapper.map(dadosParaSalvar, new TypeToken<List<DicionarioEntity>>() {
                              }.getType()));
            }
      }

}
