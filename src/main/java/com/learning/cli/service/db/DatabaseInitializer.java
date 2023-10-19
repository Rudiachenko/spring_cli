package com.learning.cli.service.db;

import com.learning.cli.model.Role;
import com.learning.cli.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Component
public class DatabaseInitializer {
    private final MongoTemplate mongoTemplate;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public DatabaseInitializer(MongoTemplate mongoTemplate, PasswordEncoder passwordEncoder) {
        this.mongoTemplate = mongoTemplate;
        this.passwordEncoder = passwordEncoder;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationEvent() {
        for (String collectionName : mongoTemplate.getCollectionNames()) {
            mongoTemplate.dropCollection(collectionName);
        }

        addObjectsToDb();
    }

    private void addObjectsToDb() {
        Role userRole = new Role(Role.RoleName.USER);
        Role adminRole = new Role(Role.RoleName.ADMIN);
        mongoTemplate.insert(userRole, "roles");
        mongoTemplate.insert(adminRole, "roles");

        Set<Role> adminRoles = new HashSet<>(Arrays.asList(userRole, adminRole));
        User adminUser = new User("admin", passwordEncoder.encode("password"), adminRoles);
        mongoTemplate.insert(adminUser, "users");
    }
}
