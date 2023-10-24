package com.learning.cli.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ComponentScan(basePackages = {
        "com.learning.cli.service",
        "com.learning.cli.security",
        "com.learning.cli.repository",
})
@Import({SecurityConfig.class, MongoDBConfig.class})
public class AppConfig {
}
