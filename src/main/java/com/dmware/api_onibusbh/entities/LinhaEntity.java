package com.dmware.api_onibusbh.entities;

import com.dmware.api_onibusbh.dto.CoordenadaDTO;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document("linhas")
public class LinhaEntity {

    @Id
    private String id;
    private Integer idLinha;
    private Integer numeroLinha;
    private String linha;
    private String nome;
    private boolean sentidoIsUnique;
    private List<CoordenadaDTO> coordenadas;

    public LinhaEntity() {
    }

    public LinhaEntity(String id, Integer idLinha, Integer numeroLinha, String linha, String nome, boolean sentidoIsUnique, List<CoordenadaDTO> coordenadas) {
        this.id = id;
        this.idLinha = idLinha;
        this.numeroLinha = numeroLinha;
        this.linha = linha;
        this.nome = nome;
        this.sentidoIsUnique = sentidoIsUnique;
        this.coordenadas = coordenadas;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getIdLinha() {
        return idLinha;
    }

    public void setIdLinha(Integer idLinha) {
        this.idLinha = idLinha;
    }

    public Integer getNumeroLinha() {
        return numeroLinha;
    }

    public void setNumeroLinha(Integer numeroLinha) {
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

    public boolean isSentidoIsUnique() {
        return sentidoIsUnique;
    }

    public void setSentidoIsUnique(boolean sentidoIsUnique) {
        this.sentidoIsUnique = sentidoIsUnique;
    }

    public List<CoordenadaDTO> getCoordenadas() {
        return coordenadas;
    }

    public void setCoordenadas(List<CoordenadaDTO> coordenadas) {
        this.coordenadas = coordenadas;
    }
}
