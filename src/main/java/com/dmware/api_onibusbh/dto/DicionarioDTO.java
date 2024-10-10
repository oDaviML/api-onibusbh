package com.dmware.api_onibusbh.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter@Setter
@AllArgsConstructor@NoArgsConstructor
public class DicionarioDTO {

      @JsonProperty("_id")
      private String id;
      @JsonProperty("Nome_do_arquivo")
      private String nomeArquivo;
      @JsonProperty("tempo_real_convencional_xxx_ddmmaahhmmss.xxx")
      private String descricao;
}
