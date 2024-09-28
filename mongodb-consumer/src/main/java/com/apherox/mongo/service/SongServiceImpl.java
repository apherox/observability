package com.apherox.mongo.service;

import com.apherox.kafka.domain.Song;
import com.apherox.mongo.domain.SongMongo;
import com.apherox.mongo.repository.SongRepository;
import io.micrometer.observation.annotation.Observed;
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

    // Example of using an annotation to observe methods
    @Observed(name = "mongodb.song.service",
            contextualName = "mongodb-song-service",
            lowCardinalityKeyValues = {"song", "song2"}
    )
    @Override
    public void saveSong(Song song) {
        try {
            SongMongo mongoSong = mapper.map(song, SongMongo.class);
            songRepository.save(mongoSong);
            log.info("Song {} successfully saved to mongodb songs database", song);
        } catch (MappingException me) {
            log.error("The song {} cannot be mapped to SongMongo: ", song, me.getMessage());
        }
    }
}
