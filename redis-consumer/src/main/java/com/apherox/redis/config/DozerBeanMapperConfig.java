package com.apherox.redis.config;

import com.apherox.kafka.domain.Song;
import com.apherox.redis.domain.SongRedis;
import org.dozer.DozerBeanMapper;
import org.dozer.loader.api.BeanMappingBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DozerBeanMapperConfig {

    @Bean
    public DozerBeanMapper mapper() {
        DozerBeanMapper mapper = new DozerBeanMapper();
        mapper.addMapping(objectMappingBuilder);
        return mapper;
    }

    BeanMappingBuilder objectMappingBuilder = new BeanMappingBuilder() {
        @Override
        protected void configure() {
            mapping(Song.class, SongRedis.class)
                    .fields("id", "id")
                    .fields("performer", "performer")
                    .fields("name", "name")
                    .fields("album", "album")
                    .fields("genre", "genre")
                    .fields("year", "year");
        }
    };

}
