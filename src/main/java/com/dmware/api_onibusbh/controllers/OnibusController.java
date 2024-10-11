package com.dmware.api_onibusbh.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.dmware.api_onibusbh.dto.OnibusDTO;
import com.dmware.api_onibusbh.services.OnibusService;

@Controller
@RequestMapping(path = "/api/v1/onibus")
public class OnibusController {

      @Autowired
      private OnibusService onibusService;

      @GetMapping("/")
      public ResponseEntity<List<OnibusDTO>> onibus() {
            return ResponseEntity.ok().body(onibusService.fetchOnibus());
      }
}
