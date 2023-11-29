package com.learning.cli.inmemory;

import com.learning.cli.config.TestMongoDbConfig;
import com.learning.cli.model.Role;
import com.learning.cli.repository.RoleRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ContextConfiguration(classes = TestMongoDbConfig.class)
public class RoleRepositoryTest {
    @Autowired
    private RoleRepository roleRepository;

    @BeforeEach
    void setUp() {
        roleRepository.insert(new Role(Role.RoleName.ADMIN));
    }

    @AfterEach
    void tearDown() {
        roleRepository.deleteAll();
    }

    @Test
    public void shouldFindRoleByRoleName() {
        Role.RoleName roleName = Role.RoleName.ADMIN;
        Role roleByRoleName = roleRepository.findRoleByRoleName(roleName);

        assertEquals(roleByRoleName.getRoleName(), roleName);
    }
}
