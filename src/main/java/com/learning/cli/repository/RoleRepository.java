package com.learning.cli.repository;

import com.learning.cli.model.Role;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RoleRepository extends MongoRepository<Role, Long> {
    Role findRoleByRoleName(Role.RoleName roleName);
}
