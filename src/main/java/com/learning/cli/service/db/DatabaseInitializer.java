package com.learning.cli.service.db;

import com.learning.cli.model.Role;
import com.learning.cli.model.User;
import com.learning.cli.service.RoleService;
import com.learning.cli.service.UserService;
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
    private final UserService userService;
    private final RoleService roleService;

    @Autowired
    public DatabaseInitializer(MongoTemplate mongoTemplate, PasswordEncoder passwordEncoder, UserService userService, RoleService roleService) {
        this.mongoTemplate = mongoTemplate;
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
        this.roleService = roleService;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationEvent() {
        for (String collectionName : mongoTemplate.getCollectionNames()) {
            mongoTemplate.dropCollection(collectionName);
        }

        addObjectsToDb();
    }

    private void addObjectsToDb() {
        Role userRole = roleService.addRole(new Role(Role.RoleName.USER));
        Role adminRole = roleService.addRole(new Role(Role.RoleName.ADMIN));

        Set<Role> adminRoles = new HashSet<>(Arrays.asList(userRole, adminRole));
        User adminUser = new User("admin", passwordEncoder.encode("password"), adminRoles);
        User user = userService.addUser(adminUser);
//        mongoTemplate.insert(adminUser, "users");
    }
}
