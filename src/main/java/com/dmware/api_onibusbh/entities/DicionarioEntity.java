package com.dmware.api_onibusbh.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("dicionario")
public class DicionarioEntity {
      @Id
      private String id;
      private String idDicionario;
      private String nomeArquivo;
      private String descricao;

      public DicionarioEntity() {
      }

      public DicionarioEntity(String id, String idDicionario, String nomeArquivo, String descricao) {
            this.id = id;
            this.idDicionario = idDicionario;
            this.nomeArquivo = nomeArquivo;
            this.descricao = descricao;
      }

      public String getId() {
            return id;
      }

      public void setId(String id) {
            this.id = id;
      }

      public String getIdDicionario() {
            return idDicionario;
      }

      public void setIdDicionario(String idDicionario) {
            this.idDicionario = idDicionario;
      }

      public String getNomeArquivo() {
            return nomeArquivo;
      }

      public void setNomeArquivo(String nomeArquivo) {
            this.nomeArquivo = nomeArquivo;
      }

      public String getDescricao() {
            return descricao;
      }

      public void setDescricao(String descricao) {
            this.descricao = descricao;
      }

}
