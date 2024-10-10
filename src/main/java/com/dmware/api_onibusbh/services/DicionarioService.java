package com.dmware.api_onibusbh.services;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import com.dmware.api_onibusbh.dto.DicionarioDTO;
import com.dmware.api_onibusbh.entities.DicionarioEntity;
import com.dmware.api_onibusbh.repositories.DicionarioRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class DicionarioService {

      @Autowired
      private DicionarioRepository dicionarioRepository;
      @Autowired
      private ModelMapper modelMapper;

      ObjectMapper objectMapper = new ObjectMapper();
      private static final Logger logger = LoggerFactory.getLogger(LinhasService.class);

      public List<DicionarioDTO> fetchDicionarios() {
            List<DicionarioEntity> dicionarioEntityList = dicionarioRepository.findAll();
            List<DicionarioDTO> dicionarioDTOList = modelMapper.map(dicionarioEntityList, new TypeToken<List<DicionarioDTO>>() {}.getType());
            return dicionarioDTOList;
      }

}
