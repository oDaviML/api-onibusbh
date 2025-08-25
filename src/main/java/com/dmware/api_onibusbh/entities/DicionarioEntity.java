package com.dmware.api_onibusbh.entities;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("dicionario")
@Data
public class DicionarioEntity {
      @Id
      private String id;
      private String idDicionario;
      private String nomeArquivo;
      private String descricao;
}
