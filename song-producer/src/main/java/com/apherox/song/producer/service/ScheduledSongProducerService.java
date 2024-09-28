package com.apherox.song.producer.service;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import io.opentelemetry.context.propagation.TextMapPropagator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author apherox
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ScheduledSongProducerService {

	@Value("${kafka-service.rest-endpoint}")
	private String restUrl;

	private final Faker faker;

	private final RestTemplate restTemplate;

	private final Tracer tracer;

	@Scheduled(cron = "*/15 * * * * *")
	public void createSong() {
		Span span = tracer.spanBuilder("song-producer")
				.setSpanKind(SpanKind.CLIENT)
				.startSpan();

		try (Scope scope = span.makeCurrent()) {
			JSONObject song = new JSONObject();
			song.put("id", UUID.randomUUID().toString());
			song.put("performer", faker.bossaNova().artist());
			song.put("name", faker.bossaNova().song());
			song.put("album", faker.artist().name());
			song.put("genre", faker.music().genre());
			song.put("year", "" + faker.date().past(80 * 368, TimeUnit.DAYS).toLocalDateTime().getYear());

			sendSong(song.toString(), span);
		} catch (Throwable t) {
			span.recordException(t);
			throw t;
		} finally {
			span.end();
		}
	}

	private void sendSong(final String song, Span span) {

		// traceparent value to be sent as an HTTP header with the request
		String traceparentHeaderValue = String.format("00-%s-%s-00", bytesToHex(span.getSpanContext().getTraceIdBytes()),
				bytesToHex(span.getSpanContext().getSpanIdBytes()));

		// Prepare HTTP request
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.put("traceparent", List.of(traceparentHeaderValue));
		HttpEntity<String> entity = new HttpEntity<>(song, headers);

		// Opentelemetry context propagation
		Context contextWithSpan = Context.current().with(span);
		TextMapPropagator propagator = GlobalOpenTelemetry.getPropagators().getTextMapPropagator();

		propagator.inject(contextWithSpan, headers, (httpHeaders, key, value) -> httpHeaders.put(key, List.of(value)));

		restTemplate.postForEntity(restUrl + "/song/create", entity, String.class);
		log.info("Song {} created.", song);
	}

	private static String bytesToHex(byte[] bytes) {
		StringBuilder hexString = new StringBuilder(2 * bytes.length);
		for (byte b : bytes) {
			hexString.append(String.format("%02x", b));
		}
		return hexString.toString();
	}
}
