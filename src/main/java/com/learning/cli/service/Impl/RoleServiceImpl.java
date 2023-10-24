package com.learning.cli.service.Impl;

import com.learning.cli.model.Role;
import com.learning.cli.repository.RoleRepository;
import com.learning.cli.service.RoleService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Log4j2
@Service
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;

    @Autowired
    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public Role findRoleByRoleName(Role.RoleName roleName) {
        Role roleByRoleName = roleRepository.findRoleByRoleName(roleName);
        log.info("Role found successfully: " + roleByRoleName);
        return roleByRoleName;
    }

    @Override
    public Role addRole(Role role) {
        Role insertedRole = roleRepository.insert(role);
        log.info("Role added successfully: " + insertedRole);
        return insertedRole;
    }
}
