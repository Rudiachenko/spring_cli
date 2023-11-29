package com.learning.cli.service;

import com.learning.cli.model.Role;
import com.learning.cli.repository.RoleRepository;
import com.learning.cli.service.Impl.RoleServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RoleServiceTest {
    @Mock
    private RoleRepository roleRepository;
    @InjectMocks
    private RoleServiceImpl roleService;

    private Role role;

    @BeforeEach
    void setUp() {
        role = new Role(Role.RoleName.USER);
    }

    @Test
    void shouldFindRoleByRoleName() {
        when(roleRepository.findRoleByRoleName(any(Role.RoleName.class))).thenReturn(role);

        Role result = roleService.findRoleByRoleName(Role.RoleName.USER);

        assertNotNull(result);
        assertEquals(Role.RoleName.USER, result.getRoleName());

        verify(roleRepository, times(1)).findRoleByRoleName(any(Role.RoleName.class));
    }

    @Test
    void shouldAddRole() {
        when(roleRepository.insert(any(Role.class))).thenReturn(role);

        Role result = roleService.addRole(role);

        assertNotNull(result);

        verify(roleRepository, times(1)).insert(any(Role.class));
    }
}
