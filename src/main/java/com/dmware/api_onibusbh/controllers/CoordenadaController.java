package com.dmware.api_onibusbh.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.dmware.api_onibusbh.dto.CoordenadaDTO;
import com.dmware.api_onibusbh.services.ApiResponse;
import com.dmware.api_onibusbh.services.CoordenadaService;

@Controller
@RequestMapping(path = "/api/v1/coordenadas")
public class CoordenadaController {

      @Autowired
      private CoordenadaService onibusService;

      @GetMapping("/")
      public ResponseEntity<ApiResponse<List<CoordenadaDTO>>> onibus() {
            return ApiResponse.of("Coordenadas sem tratamento", HttpStatus.OK, onibusService.fetchCoordenadas());
      }
}
