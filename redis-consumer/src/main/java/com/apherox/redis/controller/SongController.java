package com.apherox.redis.controller;

import com.apherox.redis.domain.SongRedis;
import com.apherox.redis.repository.SongRepository;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
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

    private final Tracer tracer;

    @GetMapping
    public ResponseEntity<List<SongRedis>> getAllSongs() {
        Span span = tracer.spanBuilder("song-producer").startSpan();
        try (Scope scope = span.makeCurrent()) {
            List<SongRedis> songs = StreamSupport.stream(songRepository.findAll().spliterator(), false)
                    .toList();
            return new ResponseEntity<>(songs.stream().toList(), OK);
        } catch (Throwable t) {
            span.recordException(t);
            throw t;
        } finally {
            span.end();
        }
    }
}
