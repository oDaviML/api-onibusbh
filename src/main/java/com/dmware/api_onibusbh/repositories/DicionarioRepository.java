package com.dmware.api_onibusbh.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.dmware.api_onibusbh.entities.DicionarioEntity;

public interface DicionarioRepository extends MongoRepository<DicionarioEntity, String> {

}
