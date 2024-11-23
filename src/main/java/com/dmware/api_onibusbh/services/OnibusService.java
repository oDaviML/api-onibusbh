package com.dmware.api_onibusbh.services;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dmware.api_onibusbh.dto.CoordenadaDTO;
import com.dmware.api_onibusbh.dto.LinhaDTO;
import com.dmware.api_onibusbh.dto.OnibusDTO;

@Service
public class OnibusService {

      @Autowired
      private LinhasService linhasService;
      @Autowired
      private CoordenadaService coordenadaService;

      public List<OnibusDTO> fetchOnibus() {
            List<LinhaDTO> linhas = linhasService.fetchLinhas();
            List<CoordenadaDTO> coordenadas = coordenadaService.fetchCoordenadas();
            List<OnibusDTO> listaOnibus = new ArrayList<>();

            // Filtra as coordenadas pelo número do veículo, deixando apenas as que forem
            // mais recentes em relação ao horário
            List<CoordenadaDTO> coordenadasFiltradas = coordenadas.stream()
                        .collect(Collectors.toMap(
                                    CoordenadaDTO::getNumeroVeiculo,
                                    Function.identity(),
                                    BinaryOperator.maxBy(Comparator.comparing(CoordenadaDTO::getHorario))))
                        .values().stream().collect(Collectors.toList());

            // Mapeia as linhas com suas respectivas coordenadas
            linhas.forEach(linha -> {
                  OnibusDTO onibus = new OnibusDTO();
                  onibus.setIdLinha(linha.getNumeroLinha());
                  onibus.setNomeLinha(linha.getNome());
                  onibus.setLinha(linha.getLinha());
                  onibus.setCoordenadas(coordenadasFiltradas.stream()
                              .filter(coord -> Objects.equals(coord.getIdOnibus(), linha.getNumeroLinha()))
                              .collect(Collectors.toList()));

                  if (!onibus.getCoordenadas().isEmpty()) {
                        listaOnibus.add(onibus);
                  }
            });

            return listaOnibus;
      };
}
