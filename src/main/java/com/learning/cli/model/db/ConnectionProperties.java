package com.learning.cli.model.db;

import org.springframework.beans.factory.annotation.Value;

public class ConnectionProperties {
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


    public String getConnectionUri() {
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
        return connectionString;
    }
}
