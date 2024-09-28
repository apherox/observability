package com.apherox.redis.service;

import com.apherox.kafka.domain.Song;
import com.apherox.redis.domain.SongRedis;
import com.apherox.redis.repository.SongRepository;
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
    public void saveSong(Song song) {
        try {
            SongRedis redisSong = mapper.map(song, SongRedis.class);
            songRepository.save(redisSong);
            log.info("Song {} successfully saved to redis database", song);
        } catch (MappingException me) {
            log.error("The song {} cannot be mapped to SongRedis: ", song, me.getMessage());
        }
    }
}
