package com.apherox.mongo.config;

import com.apherox.kafka.domain.Song;
import com.apherox.mongo.domain.SongMongo;
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
            mapping(Song.class, SongMongo.class)
                    .fields("id", "id")
                    .fields("performer", "performer")
                    .fields("name", "name")
                    .fields("album", "album")
                    .fields("genre", "genre")
                    .fields("year", "year");
        }
    };

}
