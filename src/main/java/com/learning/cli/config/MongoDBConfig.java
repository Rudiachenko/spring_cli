package com.learning.cli.config;

import com.learning.cli.model.db.ConnectionProperties;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;

@Log4j2
@Configuration
public class MongoDBConfig {
    @Bean
    public MongoDatabaseFactory mongoDatabaseFactory() {
        String connectionString = connectionProperties().getConnectionUri();
        log.info("MongoDB connection string: {}", connectionString);
        return new SimpleMongoClientDatabaseFactory(connectionString);
    }

    @Bean
    public MongoTemplate mongoTemplate(MongoDatabaseFactory mongoDatabaseFactory) {
        return new MongoTemplate(mongoDatabaseFactory);
    }

    @Bean
    public ConnectionProperties connectionProperties() {
        return new ConnectionProperties();
    }
}
