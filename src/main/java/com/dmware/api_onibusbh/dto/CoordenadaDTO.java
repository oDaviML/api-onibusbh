package com.dmware.api_onibusbh.dto;

import com.dmware.api_onibusbh.utils.DateDeserializer;
import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CoordenadaDTO {

    @JsonIgnore
    private String id;

    @JsonProperty("numeroLinha")
    @JsonAlias("NL")
    private Integer numeroLinha;

    @JsonProperty("numeroVeiculo")
    @JsonAlias("NV")
    private String numeroVeiculo;

    @JsonProperty("latitude")
    @JsonAlias("LT")
    private Double latitude;

    @JsonProperty("longitude")
    @JsonAlias("LG")
    private Double longitude;

    @JsonProperty("velocidade")
    @JsonAlias("VL")
    private Double velocidade;

    @JsonProperty("sentido")
    @JsonAlias("SV")
    private Character sentido;

    @JsonProperty("horario")
    @JsonAlias("HR")
    @JsonDeserialize(using = DateDeserializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss:SSS")
    private LocalDateTime horario;

    @Override
    public String toString() {
        return "CoordenadaDTO [id=" + id + ", numeroLinha=" + numeroLinha + ", numeroVeiculo=" + numeroVeiculo
                + ", latitude=" + latitude + ", longitude=" + longitude + ", velocidade=" + velocidade
                + ", sentido=" + sentido + ", horario=" + horario + "]";
    }
}
