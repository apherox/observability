package com.apherox.elasticsearch;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@Slf4j
@SpringBootApplication(scanBasePackages = "com.apherox.elasticsearch")
@EnableElasticsearchRepositories(basePackages = {"com.apherox.elasticsearch"})
public class ElasticsearchConsumerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ElasticsearchConsumerApplication.class, args);
        log.info("Elasticsearch consumer application started");
    }
}
