package com.dmware.api_onibusbh.scheduler;

import com.dmware.api_onibusbh.apis.DicionarioAPI;
import com.dmware.api_onibusbh.dto.DicionarioDTO;
import com.dmware.api_onibusbh.entities.DicionarioEntity;
import com.dmware.api_onibusbh.repositories.DicionarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
public class DicionarioScheduler {

      @Autowired
      private DicionarioRepository dicionarioRepository;
      @Autowired
      private DicionarioAPI dicionarioAPI;
      private static final Logger logger = LoggerFactory.getLogger(DicionarioScheduler.class);

      @Scheduled(fixedDelay = 12, timeUnit = TimeUnit.SECONDS)
      public void salvaDicionarioBanco() {
            List<DicionarioDTO> listaDados = dicionarioAPI.fetchDicionario();
            List<DicionarioEntity> dadosExistentes = dicionarioRepository.findAll();


            // Mapeia os dados da api para facilitar a comparação
            Map<String, DicionarioDTO> dicionarioMap = listaDados.stream()
                    .collect(Collectors.toMap(DicionarioDTO::getId, dicionarioDTO -> dicionarioDTO));

            // Realiza a comparação, se for diferente, insere no banco de dados
            for (DicionarioEntity dicionarioEntity : dadosExistentes ) {
                  DicionarioDTO dicionarioComparacao = dicionarioMap.get(dicionarioEntity.getIdDicionario());

                  if (dicionarioComparacao == null){
                        dicionarioRepository.save(dicionarioEntity);
                  }
                  if (!dicionarioComparacao.getNomeArquivo().equals(dicionarioEntity.getNomeArquivo())) {
                        dicionarioRepository.save(dicionarioEntity);
                  }
            }

      }
}
