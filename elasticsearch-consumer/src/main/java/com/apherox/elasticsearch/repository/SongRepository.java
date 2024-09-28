package com.apherox.elasticsearch.repository;

import com.apherox.elasticsearch.domain.SongElastic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SongRepository extends ElasticsearchRepository<SongElastic, String> {

	Page<SongElastic> findByName(String name, Pageable pageable);

	@Query("{\"bool\": {\"must\": [{\"match\": {\"name\": \"?0\"}}]}}")
	Page<SongElastic> findByNameUsingCustomQuery(String name, Pageable pageable);
}
