package com.apherox.kafka.controller;

import com.apherox.kafka.domain.Song;
import com.apherox.kafka.service.SongProducerService;
import io.micrometer.core.annotation.Timed;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import io.opentelemetry.context.propagation.TextMapGetter;
import io.opentelemetry.context.propagation.TextMapPropagator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author apherox
 */
@RestController
@Validated
@RequestMapping("/song")
@RequiredArgsConstructor
@Slf4j
@Timed
public class SongController {

	private final SongProducerService songProducerService;
	private final Tracer tracer;

	@PostMapping(path = "/create", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
	public ResponseEntity<String> createSong(@RequestHeader HttpHeaders headers, @Valid @RequestBody Song song) {
		Context extractedContext = extractContext(headers);

		if (extractedContext == null) {
			return createSongWithNewContext(song);
		}
		return createSongWithPropagatedContext(song, extractedContext, headers);
	}

	private ResponseEntity<String> createSongWithPropagatedContext(Song song, Context extractedContext, HttpHeaders requestHeaders) {
		try (Scope scope = extractedContext.makeCurrent()) {
			Span span = tracer.spanBuilder("kafka-song-controller")
					.setSpanKind(SpanKind.CONSUMER)
					.setParent(extractedContext)
					.startSpan();

			try (Scope ignored = span.makeCurrent()) {

				Map<String, String> headers = requestHeaders.entrySet().stream()
						.filter(entry -> entry.getKey().equals("traceparent"))
						.collect(Collectors.toMap(
								Map.Entry::getKey,
								entry -> entry.getValue().get(0),
								(existingValue, newValue) -> newValue // Handle duplicate keys if needed
						));
				GlobalOpenTelemetry.getPropagators().getTextMapPropagator().inject(Context.current(), headers, Map::put);

				Song retrievedSong = songProducerService.createSong(song, Optional.of(extractedContext), span);
				return ResponseEntity.ok("Song: " + retrievedSong.getName() + " created");
			} catch (Throwable t) {
				span.setStatus(StatusCode.ERROR, t.getMessage());
				span.recordException(t);
			} finally {
				span.end();
			}
		}
		return ResponseEntity.internalServerError().build();
	}

	private ResponseEntity<String> createSongWithNewContext(Song song) {
		Span span = tracer.spanBuilder("kafka-song-controller")
				.setSpanKind(SpanKind.CONSUMER)
				.startSpan();

		try (Scope scope = span.makeCurrent()) {
			Song retrievedSong = songProducerService.createSong(song, Optional.empty(), span);
			return ResponseEntity.ok("Song: " + retrievedSong.getName() + " created");
		} catch(Throwable t) {
			span.setStatus(StatusCode.ERROR, t.getMessage());
			span.recordException(t);
			throw t;
		} finally {
			span.end();
		}
	}

	private Context extractContext(HttpHeaders requestHeaders) {
		TextMapGetter<Map<String, List<String>>> getter =
				new TextMapGetter<>() {
					@Override
					public String get(Map<String, List<String>> headers, String s) {
						assert headers != null;
						return headers.get(s) != null ? headers.get(s).get(0) : "";
					}

					@Override
					public Iterable<String> keys(Map<String, List<String>> headers) {
						List<String> keys = new ArrayList<>();
						Set<String> requestHeaders = headers.keySet();
						requestHeaders.forEach(keys::add);
						return keys;
					}
				};

		Map<String, List<String>> headers = requestHeaders.entrySet().stream()
				.filter(entry -> entry.getKey().equals("traceparent"))
				.collect(Collectors.toMap(
						Map.Entry::getKey,
						Map.Entry::getValue,
						(existingValue, newValue) -> newValue // Handle duplicate keys if needed
				));

		if (!headers.isEmpty()) {
			TextMapPropagator propagator = GlobalOpenTelemetry.getPropagators().getTextMapPropagator();
			return propagator.extract(Context.current(), headers, getter);
		}
		return null;
	}
}
