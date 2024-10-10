package com.dmware.api_onibusbh.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dmware.api_onibusbh.config.WebClientConfig;
import com.dmware.api_onibusbh.dto.OnibusDTO;
import com.dmware.api_onibusbh.entities.LinhaEntity;
import com.dmware.api_onibusbh.repositories.LinhasRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class OnibusService {

      @Autowired
      private LinhasRepository linhasRepository;

      ObjectMapper objectMapper = new ObjectMapper();

      public List<OnibusDTO> fetchOnibus() {
            List<LinhaEntity> linhas = linhasRepository.findAll();
            return null;
      }

}
