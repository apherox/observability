package com.apherox.elasticsearch.config;

import io.micrometer.observation.ObservationRegistry;
import io.micrometer.observation.aop.ObservedAspect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author apherox
 */
@Configuration(proxyBeanMethods = false)
public class ObservabilityConfig {

	// To have the @Observed support we need to register this aspect
	@Bean
	ObservedAspect observedAspect(ObservationRegistry observationRegistry) {
		return new ObservedAspect(observationRegistry);
	}
}
