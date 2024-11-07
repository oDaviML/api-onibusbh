package com.dmware.api_onibusbh.dto;

import java.time.LocalDateTime;

import com.dmware.api_onibusbh.utils.DateDeserializer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

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
      private String numeroVeiculo;

      @JsonProperty("LT")
      private Double latitude;

      @JsonProperty("LG")
      private Double longitude;

      @JsonProperty("VL")
      private Double velocidade;

      @JsonProperty("SV")
      private Character sentido;

      @JsonProperty("HR")
      @JsonDeserialize(using = DateDeserializer.class)
      private LocalDateTime horario;

      @Override
      public String toString() {
            return "CoordenadaDTO [id=" + id + ", idOnibus=" + idOnibus + ", numeroVeiculo=" + numeroVeiculo
                        + ", latitude=" + latitude + ", longitude=" + longitude + ", velocidade=" + velocidade
                        + ", sentido=" + sentido + ", horario=" + horario + "]";
      }

}
