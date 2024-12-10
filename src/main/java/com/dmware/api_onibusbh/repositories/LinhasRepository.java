package com.dmware.api_onibusbh.repositories;

import com.dmware.api_onibusbh.entities.LinhaEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface LinhasRepository extends MongoRepository<LinhaEntity, String> {

    Optional<LinhaEntity> findByNumeroLinha(Integer numeroLinha);

    @Query("{ 'coordenadas': { $exists: true, $ne: [] } }")
    List<LinhaEntity> findLinhasWithCoordenadas();
}
