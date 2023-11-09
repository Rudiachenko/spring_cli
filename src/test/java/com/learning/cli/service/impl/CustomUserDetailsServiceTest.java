package com.learning.cli.service.impl;

import com.learning.cli.service.Impl.CustomUserDetailsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class CustomUserDetailsServiceTest {
    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Test
    public void testLoadUserByUsername() {
        UserDetails user = userDetailsService.loadUserByUsername("user");
        assertNotNull(user);
        assertEquals(user.getUsername(), "user");
    }
}
