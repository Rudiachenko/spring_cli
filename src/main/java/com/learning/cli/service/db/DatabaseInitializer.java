package com.learning.cli.service.db;

import com.learning.cli.model.Role;
import com.learning.cli.model.User;
import com.learning.cli.model.db.ConnectionProperties;
import com.learning.cli.service.RoleService;
import com.learning.cli.service.UserService;
import liquibase.Liquibase;
import liquibase.database.DatabaseFactory;
import liquibase.exception.LiquibaseException;
import liquibase.ext.mongodb.database.MongoLiquibaseDatabase;
import liquibase.resource.ClassLoaderResourceAccessor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Log4j2
@Component
public class DatabaseInitializer {
    private static final String CHANGELOG_MASTER = "db/changelog/db.changelog-master.yaml";
    private final ConnectionProperties connectionProperties;
    private final MongoTemplate mongoTemplate;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final RoleService roleService;

    @Autowired
    public DatabaseInitializer(ConnectionProperties connectionProperties, MongoTemplate mongoTemplate, PasswordEncoder passwordEncoder, UserService userService, RoleService roleService) {
        this.connectionProperties = connectionProperties;
        this.mongoTemplate = mongoTemplate;
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
        this.roleService = roleService;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationEvent() {
        clearDatabase();
        addObjectsToDb();
    }

    private void clearDatabase() {
        for (String collectionName : mongoTemplate.getCollectionNames()) {
            mongoTemplate.dropCollection(collectionName);
        }
        log.info("The database was successfully cleared.");
    }

    private void addObjectsToDb() {
        addTablesByLiquibase();

        Role userRole = roleService.addRole(new Role(Role.RoleName.USER));
        Role adminRole = roleService.addRole(new Role(Role.RoleName.ADMIN));

        Set<Role> adminRoles = new HashSet<>(Arrays.asList(userRole, adminRole));
        User adminUser = new User("admin", passwordEncoder.encode("password"), adminRoles);
        userService.addUser(adminUser);
    }

    private void addTablesByLiquibase() {
        String connectionUri = connectionProperties.getConnectionUri();
        try {
            MongoLiquibaseDatabase database = (MongoLiquibaseDatabase) DatabaseFactory.getInstance().openDatabase(connectionUri, null, null, null, null);
            Liquibase liquibase = new Liquibase(CHANGELOG_MASTER, new ClassLoaderResourceAccessor(), database);
            liquibase.update("");
        } catch (LiquibaseException e) {
            log.error("It's impossible to create table by liquibase");
            e.printStackTrace();
        }
    }
}
