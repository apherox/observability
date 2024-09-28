package com.apherox.song.producer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author apherox
 */
@Slf4j
@SpringBootApplication(scanBasePackages = "com.apherox.song.producer")
@EnableScheduling
public class SongProducerApplication {

	public static void main(String[] args) {
		SpringApplication.run(SongProducerApplication.class, args);
		log.info("Song Producer Application started");
	}

}
