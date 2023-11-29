package com.learning.cli.inmemory;

import com.learning.cli.config.TestMongoDbConfig;
import com.learning.cli.model.Role;
import com.learning.cli.model.User;
import com.learning.cli.repository.RoleRepository;
import com.learning.cli.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ContextConfiguration(classes = TestMongoDbConfig.class)
public class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;

    private User user;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        Role role = roleRepository.insert(new Role(Role.RoleName.USER));
        user = new User("testUser", "password", Set.of(role));
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
        roleRepository.deleteAll();
    }

    @Test
    void shouldFindByUsername() {
        userRepository.insert(user);
        Optional<User> foundUser = userRepository.findByUsername("testUser");

        assertTrue(foundUser.isPresent());
        assertEquals("testUser", foundUser.get().getUsername());
    }

    @Test
    void shouldInsertUser() {
        User insertedUser = userRepository.insert(user);
        assertNotNull(insertedUser);
        assertEquals("testUser", user.getUsername());
    }
}
