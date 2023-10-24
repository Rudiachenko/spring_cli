package com.learning.cli.config;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;

@Log4j2
@Configuration
public class MongoDBConfig {

    @Value("${spring.data.mongodb.host}")
    private String mongodbHost;

    @Value("${spring.data.mongodb.port}")
    private int mongodbPort;

    @Value("${spring.data.mongodb.database}")
    private String mongodbDatabase;

    @Value("${spring.data.mongodb.username:#{null}}")
    private String mongodbUsername;

    @Value("${spring.data.mongodb.password:#{null}}")
    private String mongodbPassword;

    @Bean
    public MongoDatabaseFactory mongoDatabaseFactory() {

        String connectionString = createConnectionUri();
        return new SimpleMongoClientDatabaseFactory(connectionString);
    }

    private String createConnectionUri() {
        String connectionString;
        if (mongodbUsername != null && mongodbPassword != null) {
            connectionString = String.format("mongodb://%s:%s@%s:%d/%s",
                    mongodbUsername,
                    mongodbPassword,
                    mongodbHost,
                    mongodbPort,
                    mongodbDatabase);
        } else {
            connectionString = String.format("mongodb://%s:%d/%s",
                    mongodbHost,
                    mongodbPort,
                    mongodbDatabase);
        }
        log.info("MongoDB connection string: {}", connectionString);
        return connectionString;
    }

    @Bean
    public MongoTemplate mongoTemplate(MongoDatabaseFactory mongoDatabaseFactory) {
        return new MongoTemplate(mongoDatabaseFactory);
    }
}
