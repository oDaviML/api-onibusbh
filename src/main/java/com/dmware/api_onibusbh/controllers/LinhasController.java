package com.dmware.api_onibusbh.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.dmware.api_onibusbh.dto.LinhaDTO;
import com.dmware.api_onibusbh.infra.CustomApiResponse;
import com.dmware.api_onibusbh.infra.ErrorResponse;
import com.dmware.api_onibusbh.services.LinhasService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Controller
@RequestMapping(path = "/api/v1/linhas", produces = "application/json")
@Tag(name = "Linhas", description = "API para listagem de linhas")
public class LinhasController {

      @Autowired
      private LinhasService linhasService;

      @Operation(summary = "Listar todas as linhas")
      @ApiResponses(value = {
                  @ApiResponse(responseCode = "200", description = "Linhas listadas"),
                  @ApiResponse(responseCode = "404", description = "Nenhuma linha encontrada", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
      })
      @GetMapping("/")
      public ResponseEntity<CustomApiResponse<List<LinhaDTO>>> listarLinhas() {
            return CustomApiResponse.of("Lista de linhas", HttpStatus.OK, linhasService.fetchLinhas());
      }
}
