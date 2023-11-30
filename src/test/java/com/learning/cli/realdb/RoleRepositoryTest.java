package com.learning.cli.realdb;

import com.learning.cli.model.Role;
import com.learning.cli.repository.RoleRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@DataMongoTest
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
