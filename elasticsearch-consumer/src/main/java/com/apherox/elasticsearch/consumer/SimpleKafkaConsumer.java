package com.apherox.elasticsearch.consumer;

import com.apherox.elasticsearch.service.SongService;
import com.apherox.kafka.domain.Song;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import io.opentelemetry.context.propagation.TextMapGetter;
import io.opentelemetry.context.propagation.TextMapPropagator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SimpleKafkaConsumer {

    private final SongService songService;

    private final Tracer tracer;

    @KafkaListener(id = "simple-kafka-music-consumer", topics = "music", groupId = "elasticsearch-song-consumer")
    public void consumeSong(Message<Song> message) {
        Objects.requireNonNull(message);
        Song song = message.getPayload();
        log.info("Elasticsearch consumer received Song: {}" + song);

        Context extractedContext = extractContext(message);

        try (Scope scope = extractedContext.makeCurrent()) {
            Span span = tracer.spanBuilder("elasticsearch-song-consumer")
                    .setSpanKind(SpanKind.CONSUMER)
                    .setParent(extractedContext)
                    .startSpan();

            try (Scope ignored = span.makeCurrent()) {

                Map<String, String> headers = message.getHeaders().entrySet().stream()
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                entry -> entry.getValue().toString(),
                                (existingValue, newValue) -> newValue // Handle duplicate keys if needed
                        ));
                GlobalOpenTelemetry.getPropagators().getTextMapPropagator().inject(Context.current(), headers, Map::put);
                songService.saveSong(song);
            } catch (Throwable t) {
                span.setStatus(StatusCode.ERROR, t.getMessage());
                span.recordException(t);
            } finally {
                span.end();
            }
        }
    }

    private Context extractContext(Message<Song> message) {
        TextMapGetter<Map<String, String>> getter =
                new TextMapGetter<>() {
                    @Override
                    public String get(Map<String, String> headers, String s) {
                        assert headers != null;
                        return headers.get(s) != null ? headers.get(s) : "";
                    }

                    @Override
                    public Iterable<String> keys(Map<String, String> headers) {
                        List<String> keys = new ArrayList<>();
                        Set<String> requestHeaders = headers.keySet();
                        requestHeaders.forEach(keys::add);
                        return keys;
                    }
                };

        Map<String, String> headers = message.getHeaders().entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().toString(),
                        (existingValue, newValue) -> newValue // Handle duplicate keys if needed
                ));

        TextMapPropagator propagator = GlobalOpenTelemetry.getPropagators().getTextMapPropagator();
        return propagator.extract(Context.current(), headers, getter);
    }
}
