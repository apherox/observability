package com.apherox.kafka.config;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * @author apherox
 */
@Configuration
public class MetricsConfig {

	@Bean
	@Primary
	public MeterRegistry meterRegistry() {
		return new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
	}
}