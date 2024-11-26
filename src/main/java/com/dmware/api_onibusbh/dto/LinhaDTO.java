package com.dmware.api_onibusbh.dto;

import com.dmware.api_onibusbh.utils.TrimStringDeserializer;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LinhaDTO {
      @JsonIgnore
      private String id;

      @JsonProperty("id")
      @JsonAlias("_id")
      private Integer idLinha;

      @JsonProperty("numeroLinha")
      @JsonAlias("NumeroLinha")
      private Integer numeroLinha;

      @JsonProperty("linha")
      @JsonAlias("Linha")
      @JsonDeserialize(using = TrimStringDeserializer.class)
      private String linha;

      @JsonProperty("nome")
      @JsonAlias("Nome")
      @JsonDeserialize(using = TrimStringDeserializer.class)
      private String nome;
}
