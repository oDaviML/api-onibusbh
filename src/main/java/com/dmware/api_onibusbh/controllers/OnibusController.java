package com.dmware.api_onibusbh.controllers;

import com.dmware.api_onibusbh.dto.CoordenadaDTO;
import com.dmware.api_onibusbh.dto.OnibusDTO;
import com.dmware.api_onibusbh.infra.CustomApiResponse;
import com.dmware.api_onibusbh.infra.ErrorResponse;
import com.dmware.api_onibusbh.services.OnibusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path = "/api/v1/onibus", produces = "application/json")
@Tag(name = "Onibus", description = "API para buscar as coordenadas dos veículos")
public class OnibusController {

    private final OnibusService onibusService;

    public OnibusController(OnibusService onibusService) {
        this.onibusService = onibusService;
    }

    @Operation(summary = "Listar todos as coordenadas, por linha")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Coordenadas listadas"),
            @ApiResponse(responseCode = "404", description = "Nenhuma coordenada encontrada", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/")
    public ResponseEntity<CustomApiResponse<List<OnibusDTO>>> listarOnibus() {
        return CustomApiResponse.of("Lista de Onibus", HttpStatus.OK, onibusService.listarTodosOnibus());
    }

    @Operation(summary = "Listar as coordenadas de uma linha")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Coordenadas listadas"),
            @ApiResponse(responseCode = "404", description = "Nenhuma coordenada encontrada", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping(value = {"/linha/{numeroLinha}", "/linha/{numeroLinha}/{sentido}"})
    public ResponseEntity<CustomApiResponse<List<CoordenadaDTO>>> listarOnibusPorLinha(
            @PathVariable("numeroLinha") Integer numeroLinha,
            @PathVariable("sentido") Optional<Integer> sentido) {
        return CustomApiResponse.of("Coordenadas da linha", HttpStatus.OK,
                onibusService.listarPorNumeroLinha(numeroLinha, sentido));
    }
}
