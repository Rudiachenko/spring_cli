package com.learning.cli.health;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

@Component
public class MongoDBHealthIndicator implements HealthIndicator {
    private final MongoTemplate mongoTemplate;

    @Autowired
    public MongoDBHealthIndicator(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Health health() {
        try {
            // A simple query to the database to check its availability.
            mongoTemplate.executeCommand("{ ping: 1 }");
            return Health.up().withDetail("Database availability", "Available").build();
        } catch (Exception e) {
            return Health.down().withDetail("Database availability", "Unavailability").withException(e).build();
        }
    }
}
