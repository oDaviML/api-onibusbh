package com.dmware.api_onibusbh.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.dmware.api_onibusbh.entities.LinhaEntity;

public interface LinhasRepository extends MongoRepository<LinhaEntity, String> {

      public Optional<LinhaEntity> findByNumeroLinha(Integer numeroLinha);

      public Optional<List<LinhaEntity>> findByLinhaContaining(String linha);

}
