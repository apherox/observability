package com.apherox.kafka.service;

import com.apherox.kafka.domain.Song;
import com.apherox.kafka.exception.SongRetrievalException;
import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.metrics.LongCounter;
import io.opentelemetry.api.metrics.Meter;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import io.opentelemetry.context.propagation.TextMapPropagator;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author apherox
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Timed
public class SongProducerService {

    private final KafkaTemplate<String, Song> kafkaTemplate;
    private final Tracer tracer;
    private final MeterRegistry meterRegistry;
    private final Meter meter;
    private Counter songsSentToTopicCounter;
    private LongCounter songsSentToKafkaTopicCounter;

    @PostConstruct
    public void init() {
        songsSentToKafkaTopicCounter = meter.counterBuilder("songs_sent_to_kafka_topic")
                .setDescription("indicates number of songs created and sent to kafka topic")
                .build();

        songsSentToTopicCounter = Counter
                .builder("songs_sent_to_kafka_topic")
                .description("indicates number of songs created and sent to kafka topic")
                .register(meterRegistry);
    }

    @Value("${app.topic.name}")
    private String topic;

    @SneakyThrows
    public Song createSong(Song song, Optional<Context> context, Span span) {
        CompletableFuture<SendResult<String, Song>> sendResult = sendMessage(song, context, span);
        sendResult.thenRun(() -> log.info("Song '{}' successfully send to kafka topic.", song.getName()));
        sendResult.thenRun(() -> {
            songsSentToKafkaTopicCounter.add(1);
            songsSentToTopicCounter.increment();
        });

        SendResult<String, Song> result;
        try {
            result = sendResult.get(2, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            log.error("Song {} can't be retrieved from kafka topic", song.getName());
            throw new SongRetrievalException(500, e.getMessage());
        }
        return result.getProducerRecord().value();
    }

    private CompletableFuture<SendResult<String, Song>> sendMessage(Song song, Optional<Context> context, Span span) {
        if (context.isEmpty()) {
            return sendMessageWithNewSpan(song);
        }
        // context and span will always be populated so no need of additional existence check
        return sendMessageWithExtractedContext(song, context.get(), span);
    }

    private CompletableFuture<SendResult<String, Song>> sendMessageWithExtractedContext(Song song, Context context, Span span) {

        // Make the span the current span
        try (Scope scope = span.makeCurrent()) {
            Message<Song> message = MessageBuilder
                    .withPayload(song)
                    .setHeader(KafkaHeaders.TOPIC, topic)
                    .setHeader("traceparent", String.format("00-%s-%s-00", bytesToHex(span.getSpanContext().getTraceIdBytes()),
                            bytesToHex(span.getSpanContext().getSpanIdBytes())))
                    .build();

            TextMapPropagator propagator = GlobalOpenTelemetry.getPropagators().getTextMapPropagator();
            Map<String, String> headers = new HashMap<>();
            message.getHeaders().entrySet().stream()
                    .forEach(entry -> headers.put(entry.getKey(), entry.getValue().toString()));

            propagator.inject(context, headers, Map::put);

            return kafkaTemplate.send(message);
        } catch(Throwable t) {
            span.setStatus(StatusCode.ERROR, t.getMessage());
            span.recordException(t);
            throw t;
        } finally {
            span.end();
        }
    }

    private CompletableFuture<SendResult<String, Song>> sendMessageWithNewSpan(Song song) {
        Span span = tracer.spanBuilder("send-song-to-kafka-topic")
                .setSpanKind(SpanKind.PRODUCER)
                .startSpan();

        // Make the span the current span
        try (Scope scope = span.makeCurrent()) {
            Message<Song> message = MessageBuilder
                    .withPayload(song)
                    .setHeader(KafkaHeaders.TOPIC, topic)
                    .setHeader("traceparent", String.format("00-%s-%s-00", bytesToHex(span.getSpanContext().getTraceIdBytes()),
                            bytesToHex(span.getSpanContext().getSpanIdBytes())))
                    .build();

            Context contextWithSpan = Context.current().with(span);
            TextMapPropagator propagator = GlobalOpenTelemetry.getPropagators().getTextMapPropagator();
            Map<String, String> headers = new HashMap<>();
            message.getHeaders().entrySet().stream()
                    .forEach(entry -> headers.put(entry.getKey(), entry.getValue().toString()));

            propagator.inject(contextWithSpan, headers, Map::put);

            return kafkaTemplate.send(message);
        } catch(Throwable t) {
            span.setStatus(StatusCode.ERROR, t.getMessage());
            span.recordException(t);
            throw t;
        } finally {
            span.end();
        }
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder(2 * bytes.length);
        for (byte b : bytes) {
            hexString.append(String.format("%02x", b));
        }
        return hexString.toString();
    }

}
