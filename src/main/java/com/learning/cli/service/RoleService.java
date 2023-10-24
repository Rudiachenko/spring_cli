package com.learning.cli.service;

import com.learning.cli.model.Role;

public interface RoleService {
    Role findRoleByRoleName(Role.RoleName roleName);

    Role addRole(Role role);
}
