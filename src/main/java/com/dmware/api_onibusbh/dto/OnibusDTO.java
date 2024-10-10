package com.dmware.api_onibusbh.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class OnibusDTO {

      @JsonIgnore
      private String id;

      private Integer idOnibus;

      private Integer numeroVeiculo;

      private Double latitude;

      private Double longitude;

      private Double velocidade;

      private Character sentido;

}
