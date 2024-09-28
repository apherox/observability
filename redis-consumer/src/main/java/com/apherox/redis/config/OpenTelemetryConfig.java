package com.apherox.redis.config;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.baggage.propagation.W3CBaggagePropagator;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.api.trace.propagation.W3CTraceContextPropagator;
import io.opentelemetry.context.propagation.ContextPropagators;
import io.opentelemetry.context.propagation.TextMapPropagator;
import io.opentelemetry.exporter.logging.LoggingMetricExporter;
import io.opentelemetry.exporter.logging.SystemOutLogRecordExporter;
import io.opentelemetry.exporter.otlp.http.trace.OtlpHttpSpanExporter;
import io.opentelemetry.exporter.zipkin.ZipkinSpanExporter;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.logs.SdkLoggerProvider;
import io.opentelemetry.sdk.logs.export.BatchLogRecordProcessor;
import io.opentelemetry.sdk.metrics.SdkMeterProvider;
import io.opentelemetry.sdk.metrics.export.PeriodicMetricReader;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.SimpleSpanProcessor;
import io.opentelemetry.semconv.ResourceAttributes;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

/**
 * @author apherox
 */
@Configuration
public class OpenTelemetryConfig {

	public static final String TRACER_NAME = "observability";

	public static final String VERSION = "1.0.0";

	@Value("${zipkin.endpoint}")
	private String zipkinEndpoint;

	@Value("${jaeger.endpoint}")
	private String jaegerEndpoint;

	@Bean(name = "jaegerExporter")
	public OtlpHttpSpanExporter otlpHttpSpanExporter(@Value("${jaeger.endpoint}") String jaegerEndpoint) {
		return OtlpHttpSpanExporter.builder()
				.setEndpoint(jaegerEndpoint)
				.build();
	}

	@Bean(name = "zipkinExporter")
	public ZipkinSpanExporter zipkinSpanExporter(@Value("${zipkin.endpoint}") String zipkinEndpoint) {
		return ZipkinSpanExporter.builder()
				.setEndpoint(zipkinEndpoint)
				.build();
	}

	@Bean(name = "openTelemetry")
	@DependsOn(value = {"jaegerExporter", "zipkinExporter"})
	public OpenTelemetry openTelemetry() {
		Resource resource = Resource.getDefault().toBuilder()
				.put(ResourceAttributes.SERVICE_NAME, "redis-consumer")
				.put(ResourceAttributes.SERVICE_VERSION, "1.0.0")
				.build();

		ZipkinSpanExporter zipkinSpanExporter = ZipkinSpanExporter.builder()
				.setEndpoint(zipkinEndpoint)
				.build();

		OtlpHttpSpanExporter jaegerSpanExporter = OtlpHttpSpanExporter.builder()
				.setEndpoint(jaegerEndpoint)
				.build();

		SdkTracerProvider sdkTracerProvider = SdkTracerProvider.builder()
				.addSpanProcessor(SimpleSpanProcessor.create(zipkinSpanExporter))
				.addSpanProcessor(SimpleSpanProcessor.create(jaegerSpanExporter))
				.setResource(resource)
				.build();

		SdkMeterProvider sdkMeterProvider = SdkMeterProvider.builder()
				.registerMetricReader(PeriodicMetricReader.builder(LoggingMetricExporter.create()).build())
				.setResource(resource)
				.build();

		SdkLoggerProvider sdkLoggerProvider = SdkLoggerProvider.builder()
				.addLogRecordProcessor(BatchLogRecordProcessor.builder(SystemOutLogRecordExporter.create()).build())
				.setResource(resource)
				.build();

		OpenTelemetry openTelemetry = OpenTelemetrySdk.builder()
				.setTracerProvider(sdkTracerProvider)
				.setMeterProvider(sdkMeterProvider)
				.setLoggerProvider(sdkLoggerProvider)
				.setPropagators(ContextPropagators.create(TextMapPropagator.composite(W3CTraceContextPropagator.getInstance(), W3CBaggagePropagator.getInstance())))
				.buildAndRegisterGlobal();

		return openTelemetry;
	}

	@Bean
	@DependsOn(value = "openTelemetry")
	public Tracer getTracer() {
		return openTelemetry().getTracer(TRACER_NAME, VERSION);
	}
}
