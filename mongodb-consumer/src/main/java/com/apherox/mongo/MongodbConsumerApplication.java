package com.apherox.mongo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication(scanBasePackages = "com.apherox.mongo")
public class MongodbConsumerApplication {

    public static void main(String[] args) {
        SpringApplication.run(MongodbConsumerApplication.class, args);
        log.info("Mongodb consumer application started");
    }
}
