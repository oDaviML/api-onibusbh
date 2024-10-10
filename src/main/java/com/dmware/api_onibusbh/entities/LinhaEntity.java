package com.dmware.api_onibusbh.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("linhas")
public class LinhaEntity {

      @Id
      private String id;
      // TODO: Adicionar tipo (BH OU Metropolitano)
      private Long idLinha;
      private Long numeroLinha;
      private String linha;
      private String nome;

      public LinhaEntity(String id, Long idLinha, Long numeroLinha, String linha, String nome) {
            this.id = id;
            this.idLinha = idLinha;
            this.numeroLinha = numeroLinha;
            this.linha = linha;
            this.nome = nome;
      }

      public LinhaEntity() {
      }

      public String getId() {
            return id;
      }

      public void setId(String id) {
            this.id = id;
      }

      public Long getIdLinha() {
            return idLinha;
      }

      public void setIdLinha(Long idLinha) {
            this.idLinha = idLinha;
      }

      public Long getNumeroLinha() {
            return numeroLinha;
      }

      public void setNumeroLinha(Long numeroLinha) {
            this.numeroLinha = numeroLinha;
      }

      public String getLinha() {
            return linha;
      }

      public void setLinha(String linha) {
            this.linha = linha;
      }

      public String getNome() {
            return nome;
      }

      public void setNome(String nome) {
            this.nome = nome;
      }

}
