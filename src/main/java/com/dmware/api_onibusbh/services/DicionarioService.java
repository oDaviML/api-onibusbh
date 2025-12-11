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

import static net.logstash.logback.argument.StructuredArguments.kv;

@Service
public class DicionarioService {

    private final DicionarioRepository dicionarioRepository;
    private final ModelMapper modelMapper;
    private final APIService apiService;

    private static final Logger logger = LoggerFactory.getLogger(DicionarioService.class);

    public DicionarioService(DicionarioRepository dicionarioRepository, ModelMapper modelMapper, APIService apiService) {
        this.dicionarioRepository = dicionarioRepository;
        this.modelMapper = modelMapper;
        this.apiService = apiService;
    }

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
            logger.info("Estado inicial do banco", kv("total_existente", dadosExistentes.size()));

            Map<String, DicionarioEntity> dicionarioMap = dadosExistentes.stream()
                        .collect(Collectors.toMap(DicionarioEntity::getIdDicionario,
                                    dicionarioEntity -> dicionarioEntity));

            List<DicionarioDTO> dadosParaSalvar = new ArrayList<>();

            for (DicionarioDTO dicionarioDTO : listaDados) {
                  DicionarioEntity dicionarioComparacao = dicionarioMap.get(dicionarioDTO.getId());

                  if (dicionarioComparacao == null) {
                        logger.info("Novo dicionário identificado", 
                            kv("nome_arquivo", dicionarioDTO.getNomeArquivo()), 
                            kv("tipo_operacao", "insercao"));
                        dadosParaSalvar.add(dicionarioDTO);
                  } else if (!dicionarioDTO.getNomeArquivo().equals(dicionarioComparacao.getNomeArquivo())) {
                        logger.info("Dicionário atualizado identificado", 
                            kv("nome_arquivo", dicionarioDTO.getNomeArquivo()), 
                            kv("tipo_operacao", "atualizacao"));
                        dadosParaSalvar.add(dicionarioDTO);
                  }
            }

            if (!dadosParaSalvar.isEmpty()) {
                  dicionarioRepository
                              .saveAll(modelMapper.map(dadosParaSalvar, new TypeToken<List<DicionarioEntity>>() {
                              }.getType()));
                  logger.info("Sincronização de dicionário concluída. Total de itens processados: {}", dadosParaSalvar.size(), kv("total_processado", dadosParaSalvar.size()));
            } else {
                  logger.info("Nenhuma alteração necessária no dicionário");
            }
      }

}
