package com.dmware.api_onibusbh.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.dmware.api_onibusbh.entities.LinhaEntity;

public interface LinhasRepository extends MongoRepository<LinhaEntity, Long> {

}
