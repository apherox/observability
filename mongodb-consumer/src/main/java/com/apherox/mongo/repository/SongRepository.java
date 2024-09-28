package com.apherox.mongo.repository;

import com.apherox.mongo.domain.SongMongo;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SongRepository extends CrudRepository<SongMongo, String> {
}
