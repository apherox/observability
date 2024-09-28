package com.apherox.redis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication(scanBasePackages = "com.apherox.redis")
public class RedisConsumerApplication {

    public static void main(String[] args) {
        SpringApplication.run(RedisConsumerApplication.class, args);
        log.info("Redis consumer application started");
    }
}
