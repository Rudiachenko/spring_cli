package com.learning.cli.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class ExternalServiceHealthIndicator implements HealthIndicator {
    private final String SERVICE_URL = "https://www.yakaboo.ua/";

    @Override
    public Health health() {
        try {
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getForObject(SERVICE_URL, String.class);
            return Health.up().withDetail("Yakaboo service", "Resource available").build();
        } catch (Exception e) {
            return Health.down().withDetail("Yakaboo service", "Resource unavailable").withException(e).build();
        }
    }
}
