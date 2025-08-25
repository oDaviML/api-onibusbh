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
    private Tipo tipo;
    private List<CoordenadaDTO> coordenadas;
}
