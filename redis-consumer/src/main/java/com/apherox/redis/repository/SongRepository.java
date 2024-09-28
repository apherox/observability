package com.apherox.redis.repository;

import com.apherox.redis.domain.SongRedis;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SongRepository extends CrudRepository<SongRedis, String> {
}
