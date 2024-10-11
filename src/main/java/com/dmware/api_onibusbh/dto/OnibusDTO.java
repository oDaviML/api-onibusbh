package com.dmware.api_onibusbh.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OnibusDTO {

      @JsonIgnore
      private String id;

      @JsonProperty("NL")
      private Integer idOnibus;

      @JsonProperty("NV")
      private Integer numeroVeiculo;

      @JsonProperty("LT")
      private Double latitude;

      @JsonProperty("LG")
      private Double longitude;

      @JsonProperty("VL")
      private Double velocidade;

      @JsonProperty("SV")
      private Character sentido;

}
