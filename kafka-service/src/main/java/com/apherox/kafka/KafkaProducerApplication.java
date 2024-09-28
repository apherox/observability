package com.apherox.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author apherox
 */
@Slf4j
@SpringBootApplication(scanBasePackages = "com.apherox.kafka")
public class KafkaProducerApplication {

    public static void main(String[] args) {
        SpringApplication.run(KafkaProducerApplication.class, args);
        log.info("Kafka Producer Application started");
    }
}
