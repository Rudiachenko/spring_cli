package com.learning.cli.config;

import com.mongodb.client.MongoClients;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.ImmutableMongodConfig;
import de.flapdoodle.embed.mongo.config.MongodConfig;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.io.IOException;

import static org.springframework.test.util.TestSocketUtils.findAvailableTcpPort;

@Configuration
@EnableMongoRepositories(basePackages = "com.learning.cli.repository")
public class TestMongoDbConfig {
    private static final String CONNECTION_STRING = "mongodb://%s:%d";

    @Bean
    public MongoTemplate mongoTemplate() throws IOException {
        String ip = "localhost";
        int randomPort = findAvailableTcpPort();

        ImmutableMongodConfig mongodConfig = MongodConfig
                .builder()
                .version(Version.Main.V6_0)
                .net(new Net(ip, randomPort, Network.localhostIsIPv6()))
                .build();

        MongodStarter starter = MongodStarter.getDefaultInstance();
        MongodExecutable mongodExecutable = starter.prepare(mongodConfig);
        mongodExecutable.start();
        return new MongoTemplate(MongoClients.create(String.format(CONNECTION_STRING, ip, randomPort)), "test");
    }
}
