package com.dmware.api_onibusbh.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.dmware.api_onibusbh.dto.DicionarioDTO;
import com.dmware.api_onibusbh.services.DicionarioService;

@Controller
@RequestMapping(path = "/api/v1/dicionario")
public class DicionarioController {

      @Autowired
      private DicionarioService dicionarioService;

      @GetMapping("/")
      public ResponseEntity<List<DicionarioDTO>> listarDicionario() {
            return ResponseEntity.ok().body(dicionarioService.fetchDicionarios());
      }

}
