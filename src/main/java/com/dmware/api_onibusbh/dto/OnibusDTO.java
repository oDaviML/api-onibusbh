package com.dmware.api_onibusbh.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OnibusDTO {
    private Integer numeroLinha;
    private String nomeLinha;
    private String linha;
    private List<CoordenadaDTO> coordenadas;
}
