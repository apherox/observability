package com.apherox.mongo.service;


import com.apherox.kafka.domain.Song;

public interface SongService {

    void saveSong(Song song);
}
