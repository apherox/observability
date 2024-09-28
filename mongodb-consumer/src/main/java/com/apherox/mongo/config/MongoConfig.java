package com.apherox.mongo.config;

import com.apherox.mongo.repository.SongRepository;
import com.mongodb.client.ListDatabasesIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.Collection;
import java.util.Collections;

@Configuration
@EnableMongoRepositories(basePackageClasses = SongRepository.class)
public class MongoConfig extends AbstractMongoClientConfiguration {

    @Value("${spring.data.mongodb.uri}")
    private String uri;

    @Value("${spring.data.mongodb.database}")
    private String db;

    @Override
    protected String getDatabaseName() {
        return "songs";
    }

    @Override
    public MongoClient mongoClient() {
        MongoClient client = MongoClients.create(uri);
        ListDatabasesIterable<Document> databases = client.listDatabases();
        databases.forEach(System.out::println);
        return client;
    }

    @Override
    public Collection<String> getMappingBasePackages() {
        return Collections.singleton("com.apherox.mongo");
    }
}
