package com.dmware.api_onibusbh.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CoordenadaDTO {

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

      @Override
      public String toString() {
            return "OnibusDTO [id=" + id + ", idOnibus=" + idOnibus + ", numeroVeiculo="
                        + numeroVeiculo + ", latitude=" + latitude + ", longitude=" + longitude + ", velocidade="
                        + velocidade + ", sentido=" + sentido + "]";
      }

}
