package com.apherox.mongo.controller;

import com.apherox.mongo.domain.SongMongo;
import com.apherox.mongo.repository.SongRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.StreamSupport;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/song")
@RequiredArgsConstructor
public class SongController {

    private final SongRepository songRepository;

    @GetMapping
    public ResponseEntity<List<SongMongo>> getAllSongs() {
            List<SongMongo> songs = StreamSupport.stream(songRepository.findAll().spliterator(), false)
                    .toList();
            return new ResponseEntity<>(songs.stream().toList(), OK);
    }
}
