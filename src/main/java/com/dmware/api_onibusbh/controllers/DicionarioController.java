package com.dmware.api_onibusbh.controllers;

import java.util.List;

import com.dmware.api_onibusbh.infra.CustomApiResponse;
import com.dmware.api_onibusbh.infra.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.dmware.api_onibusbh.dto.DicionarioDTO;
import com.dmware.api_onibusbh.services.DicionarioService;

@Controller
@RequestMapping(path = "/api/v1/dicionario")
@Tag(name = "Dicionário", description = "Dicionário de dados para as coordenadas")
public class DicionarioController {

    @Autowired
    private DicionarioService dicionarioService;

    @Operation(summary = "Retorna o dicionário para as siglas utilizadas na listagem de coordenadas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Retorna o dicionário"),
    })
    @GetMapping("/")
    public ResponseEntity<CustomApiResponse<List<DicionarioDTO>>> listarDicionario() {
        return CustomApiResponse.of("Dicionário", HttpStatus.OK, dicionarioService.fetchDicionarios());
    }

}