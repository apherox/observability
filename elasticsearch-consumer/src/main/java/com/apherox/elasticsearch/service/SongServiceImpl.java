package com.apherox.elasticsearch.service;

import com.apherox.elasticsearch.domain.SongElastic;
import com.apherox.elasticsearch.repository.SongRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dozer.Mapper;
import org.dozer.MappingException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SongServiceImpl implements SongService {

    private final SongRepository songRepository;

    private final Mapper mapper;

    @Override
    public void saveSong(com.kafka.domain.Song song) {
        try {
            SongElastic elasticSong = mapper.map(song, SongElastic.class);
            songRepository.save(elasticSong);
            log.info("Song {} successfully saved to elasticsearch songs database", song);
        } catch (MappingException me) {
            log.error("The song {} cannot be mapped to SongElastic: ", song, me.getMessage());
        }
    }
}
