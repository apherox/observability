package com.apherox.song.producer.config;

import net.datafaker.Faker;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * @author apherox
 */

@Configuration
public class AppConfig {

	@Bean
	public RestTemplate getRestTemplate() {
		return new RestTemplate();
	}

	@Bean(name = "faker")
	public Faker createFaker() {
		return new Faker();
	}
}
