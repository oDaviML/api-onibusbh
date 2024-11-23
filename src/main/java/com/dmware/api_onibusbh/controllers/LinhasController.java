package com.dmware.api_onibusbh.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.dmware.api_onibusbh.dto.LinhaDTO;
import com.dmware.api_onibusbh.infra.ApiResponse;
import com.dmware.api_onibusbh.services.LinhasService;

@Controller
@RequestMapping(path = "/api/v1/linhas")
public class LinhasController {

      @Autowired
      private LinhasService linhasService;

      @GetMapping("/")
      public ResponseEntity<ApiResponse<List<LinhaDTO>>> listarLinhas() {
            return ApiResponse.of("Lista de linhas", HttpStatus.OK, linhasService.fetchLinhas());
      }
}
